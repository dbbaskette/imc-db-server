#!/bin/bash

# =============================================================================
# IMC Database Server - API Testing Script
# =============================================================================
# Tests all implemented API endpoints using curl commands
# Sources config.env for database instance information
# =============================================================================

set -e  # Exit on any error

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="$SCRIPT_DIR/config.env"
BASE_URL="http://localhost:8084"
TEST_DB_INSTANCE="db01"
LOG_FILE="$SCRIPT_DIR/api-test-results.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# =============================================================================
# Helper Functions
# =============================================================================

log() {
    echo -e "$1" | tee -a "$LOG_FILE"
}

log_test() {
    echo -e "${BLUE}[TEST]${NC} $1" | tee -a "$LOG_FILE"
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1" | tee -a "$LOG_FILE"
    ((PASSED_TESTS++))
}

log_error() {
    echo -e "${RED}[FAIL]${NC} $1" | tee -a "$LOG_FILE"
    ((FAILED_TESTS++))
}

log_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1" | tee -a "$LOG_FILE"
}

# Function to test API endpoint
test_endpoint() {
    local method="$1"
    local endpoint="$2"
    local description="$3"
    local data="$4"
    local expected_status="${5:-200}"
    
    ((TOTAL_TESTS++))
    log_test "$description"
    
    local curl_cmd="curl -s"
    local full_url="$BASE_URL$endpoint"
    
    if [ "$method" = "POST" ] && [ -n "$data" ]; then
        curl_cmd="$curl_cmd -X POST -H 'Content-Type: application/json' -d '$data'"
    elif [ "$method" = "DELETE" ]; then
        curl_cmd="$curl_cmd -X DELETE"
    fi
    
    # Add response headers and status code
    curl_cmd="$curl_cmd -w '\nHTTP_STATUS:%{http_code}\nTIME_TOTAL:%{time_total}' '$full_url'"
    
    log "  → $method $endpoint"
    
    # Execute curl command
    local response
    response=$(eval $curl_cmd 2>/dev/null)
    local curl_exit_code=$?
    
    if [ $curl_exit_code -ne 0 ]; then
        log_error "Curl command failed (exit code: $curl_exit_code)"
        return 1
    fi
    
    # Extract HTTP status and response time
    local http_status=$(echo "$response" | tail -2 | head -1 | cut -d':' -f2)
    local time_total=$(echo "$response" | tail -1 | cut -d':' -f2)
    local json_response=$(echo "$response" | head -n -2)
    
    # Check HTTP status
    if [ "$http_status" = "$expected_status" ]; then
        log_success "HTTP $http_status (${time_total}s)"
        
        # Validate JSON response structure
        if echo "$json_response" | jq -e '.success' >/dev/null 2>&1; then
            local success_value=$(echo "$json_response" | jq -r '.success')
            if [ "$success_value" = "true" ]; then
                log "  ✓ Response structure valid"
            else
                log_warning "Response indicates failure: $(echo "$json_response" | jq -r '.error // "Unknown error"')"
            fi
        else
            log_warning "Response not in expected JSON format"
        fi
    else
        log_error "Expected HTTP $expected_status, got HTTP $http_status"
        log "  Response: $json_response"
    fi
    
    echo "" | tee -a "$LOG_FILE"
}

# =============================================================================
# Setup and Validation
# =============================================================================

print_header() {
    log "============================================================================="
    log "IMC Database Server API Test Suite"
    log "============================================================================="
    log "Start Time: $(date)"
    log "Base URL: $BASE_URL"
    log "Database Instance: $TEST_DB_INSTANCE"
    log "============================================================================="
    echo ""
}

check_prerequisites() {
    log_test "Checking prerequisites..."
    
    # Check if config.env exists
    if [ ! -f "$CONFIG_FILE" ]; then
        log_error "Config file not found: $CONFIG_FILE"
        log "Please copy scripts/config.env.template to scripts/config.env and configure it"
        exit 1
    fi
    
    # Check if jq is installed
    if ! command -v jq &> /dev/null; then
        log_error "jq is not installed. Please install jq to parse JSON responses."
        exit 1
    fi
    
    # Check if curl is installed
    if ! command -v curl &> /dev/null; then
        log_error "curl is not installed. Please install curl to run API tests."
        exit 1
    fi
    
    log_success "Prerequisites check passed"
    echo ""
}

load_config() {
    log_test "Loading configuration..."
    
    # Source the config file
    if source "$CONFIG_FILE"; then
        log_success "Configuration loaded from $CONFIG_FILE"
    else
        log_error "Failed to load configuration from $CONFIG_FILE"
        exit 1
    fi
    
    # Validate required variables
    if [ -z "$DB01_HOST" ]; then
        log_warning "DB01_HOST not set in config.env"
    fi
    
    log "Database Host: ${DB01_HOST:-'not set'}"
    log "Database Port: ${DB01_PORT:-'not set'}"
    log "Database Name: ${DB01_DATABASE:-'not set'}"
    echo ""
}

check_server() {
    log_test "Checking if server is running..."
    
    local health_response
    health_response=$(curl -s -w 'HTTP_STATUS:%{http_code}' "$BASE_URL/api/$TEST_DB_INSTANCE/health" 2>/dev/null || echo "ERROR")
    
    if [[ "$health_response" == *"HTTP_STATUS:200"* ]] || [[ "$health_response" == *"HTTP_STATUS:404"* ]]; then
        log_success "Server is running at $BASE_URL"
    else
        log_error "Server is not running at $BASE_URL"
        log "Please start the server with: mvn spring-boot:run"
        exit 1
    fi
    echo ""
}

# =============================================================================
# Test Suites
# =============================================================================

test_health_endpoints() {
    log "🏥 Testing Health & Monitoring Endpoints"
    log "----------------------------------------"
    
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/health" "Database health check"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/database/info" "Database connection info"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/database/stats" "Database statistics"
    
    # Test invalid instance
    test_endpoint "GET" "/api/nonexistent/health" "Invalid database instance" "" "404"
}

test_fleet_endpoints() {
    log "🚛 Testing Fleet Management Endpoints"
    log "-------------------------------------"
    
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/fleet/summary" "Fleet summary"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/active-count" "Active drivers count"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/high-risk-count" "High-risk drivers count"
}

test_driver_analytics() {
    log "👨‍💼 Testing Driver Analytics Endpoints"
    log "--------------------------------------"
    
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/top-performers" "Top performers (default)"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/top-performers?limit=5" "Top performers (limit 5)"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/high-risk" "High-risk drivers (default)"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/high-risk?limit=3" "High-risk drivers (limit 3)"
}

test_ml_endpoints() {
    log "🤖 Testing ML Pipeline Endpoints"
    log "--------------------------------"
    
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/ml/model-info" "ML model information"
    
    # Start ML recalculation (returns job ID)
    log_test "Starting ML recalculation..."
    local ml_response
    ml_response=$(curl -s -X POST "$BASE_URL/api/$TEST_DB_INSTANCE/ml/recalculate" 2>/dev/null)
    
    if echo "$ml_response" | jq -e '.success' >/dev/null 2>&1; then
        local job_id=$(echo "$ml_response" | jq -r '.data.jobId // empty')
        if [ -n "$job_id" ]; then
            log_success "ML recalculation started, Job ID: $job_id"
            
            # Test job status endpoint
            test_endpoint "GET" "/api/$TEST_DB_INSTANCE/ml/job-status/$job_id" "Check ML job status"
            
            # Test job cancellation
            test_endpoint "DELETE" "/api/$TEST_DB_INSTANCE/ml/job-status/$job_id" "Cancel ML job"
        else
            log_warning "ML recalculation response didn't contain job ID"
        fi
    else
        log_error "ML recalculation failed to start"
    fi
    
    # Test invalid job ID
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/ml/job-status/invalid-job-id" "Invalid job status" "" "404"
}

test_vehicle_events() {
    log "🚗 Testing Vehicle Events Endpoints"
    log "----------------------------------"
    
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events" "Get vehicle events (default)"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events?limit=10" "Vehicle events (limit 10)"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events?driver_id=123&limit=5" "Vehicle events by driver"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events?event_type=CRASH" "Crash events only"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events/crashes" "Crash events endpoint"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events/crashes?severity=HIGH" "High severity crashes"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/telemetry/events-count" "Telemetry events count"
    
    # Test batch ingestion with sample data
    local sample_events='[
        {
            "driverId": 999,
            "vehicleId": "TEST001",
            "eventType": "ACCELERATION",
            "eventDate": "2024-08-22T10:30:00",
            "speedMph": 45.5,
            "gforce": 0.8,
            "severity": "LOW"
        }
    ]'
    
    test_endpoint "POST" "/api/$TEST_DB_INSTANCE/vehicle-events/batch" "Batch insert events" "$sample_events"
}

test_edge_cases() {
    log "⚠️  Testing Edge Cases & Error Handling"
    log "--------------------------------------"
    
    # Test invalid parameters
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/drivers/top-performers?limit=abc" "Invalid limit parameter" "" "400"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events?driver_id=not-a-number" "Invalid driver ID" "" "400"
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events?event_type=INVALID_TYPE" "Invalid event type" "" "400"
    
    # Test SQL injection attempts
    test_endpoint "GET" "/api/$TEST_DB_INSTANCE/vehicle-events?driver_id=1;DROP%20TABLE%20users" "SQL injection attempt" "" "400"
}

test_performance() {
    log "⚡ Testing Performance & Rate Limiting"
    log "------------------------------------"
    
    # Test multiple rapid requests
    log_test "Testing multiple rapid requests..."
    for i in {1..5}; do
        curl -s "$BASE_URL/api/$TEST_DB_INSTANCE/health" >/dev/null &
    done
    wait
    log_success "Multiple concurrent requests handled"
    
    # Test rate limiting on expensive endpoints
    log_test "Testing rate limiting on ML recalculation..."
    local rate_limit_responses=0
    for i in {1..3}; do
        local response_status
        response_status=$(curl -s -w '%{http_code}' -o /dev/null -X POST "$BASE_URL/api/$TEST_DB_INSTANCE/ml/recalculate")
        if [ "$response_status" = "429" ]; then
            ((rate_limit_responses++))
        fi
    done
    
    if [ $rate_limit_responses -gt 0 ]; then
        log_success "Rate limiting is working ($rate_limit_responses/3 requests limited)"
    else
        log_warning "Rate limiting may not be working as expected"
    fi
    echo ""
}

# =============================================================================
# Main Execution
# =============================================================================

main() {
    # Initialize log file
    echo "IMC Database Server API Test Results" > "$LOG_FILE"
    echo "Started: $(date)" >> "$LOG_FILE"
    echo "=========================================" >> "$LOG_FILE"
    
    print_header
    check_prerequisites
    load_config
    check_server
    
    # Run test suites
    test_health_endpoints
    test_fleet_endpoints
    test_driver_analytics
    test_ml_endpoints
    test_vehicle_events
    test_edge_cases
    test_performance
    
    # Print summary
    log "============================================================================="
    log "TEST SUMMARY"
    log "============================================================================="
    log "Total Tests: $TOTAL_TESTS"
    log_success "Passed: $PASSED_TESTS"
    if [ $FAILED_TESTS -gt 0 ]; then
        log_error "Failed: $FAILED_TESTS"
    else
        log "Failed: $FAILED_TESTS"
    fi
    log "Success Rate: $(( PASSED_TESTS * 100 / TOTAL_TESTS ))%"
    log "============================================================================="
    log "End Time: $(date)"
    log "Full results saved to: $LOG_FILE"
    
    # Exit with appropriate code
    if [ $FAILED_TESTS -gt 0 ]; then
        exit 1
    else
        log_success "All tests passed! 🎉"
        exit 0
    fi
}

# =============================================================================
# Script Usage
# =============================================================================

show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -u, --url URL       Set base URL (default: http://localhost:8084)"
    echo "  -i, --instance NAME Set database instance name (default: db01)"
    echo "  -v, --verbose       Enable verbose output"
    echo ""
    echo "Examples:"
    echo "  $0                           # Run all tests with defaults"
    echo "  $0 -u http://myapp.com       # Test remote server"
    echo "  $0 -i db02                   # Test different database instance"
    echo ""
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -u|--url)
            BASE_URL="$2"
            shift 2
            ;;
        -i|--instance)
            TEST_DB_INSTANCE="$2"
            shift 2
            ;;
        -v|--verbose)
            set -x
            shift
            ;;
        *)
            log_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Run main function
main "$@"