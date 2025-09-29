#!/bin/bash

BASE_URL="http://localhost:8080"
KEYCLOAK_URL="http://localhost:9090"

echo "🎫 Ticket OAuth2 Demo - API Testing Script"
echo "========================================="

# Function to get access token
get_token() {
    local username=$1
    local password=$2
    local response=$(curl -s -X POST \
        "${KEYCLOAK_URL}/realms/ticket-realm/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=password" \
        -d "client_id=ticket-client" \
        -d "username=${username}" \
        -d "password=${password}")

    echo $response | grep -o '"access_token":"[^"]*' | cut -d'"' -f4
}

# Function to test API endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local token=$3
    local data=$4
    local description=$5

    echo ""
    echo "Testing: $description"
    echo "Endpoint: $method $endpoint"

    if [ -n "$data" ]; then
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X $method \
            "${BASE_URL}${endpoint}" \
            -H "Authorization: Bearer $token" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X $method \
            "${BASE_URL}${endpoint}" \
            -H "Authorization: Bearer $token")
    fi

    http_status=$(echo "$response" | grep "HTTP_STATUS" | cut -d: -f2)
    response_body=$(echo "$response" | sed '/HTTP_STATUS/d')

    echo "Status: $http_status"
    echo "Response: $response_body"
    echo "---"
}

echo ""
echo "1. Getting access tokens..."

# Get USER token
echo "Getting USER token..."
USER_TOKEN=$(get_token "user1" "password123")
if [ -z "$USER_TOKEN" ]; then
    echo "❌ Failed to get USER token"
    exit 1
fi
echo "✅ USER token obtained"

# Get ADMIN token
echo "Getting ADMIN token..."
ADMIN_TOKEN=$(get_token "admin1" "admin123")
if [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Failed to get ADMIN token"
    exit 1
fi
echo "✅ ADMIN token obtained"

echo ""
echo "2. Testing API endpoints..."

# Test user info
test_endpoint "GET" "/api/tickets/me" "$USER_TOKEN" "" "Get current user info (USER)"

# Test creating tickets
test_endpoint "POST" "/api/tickets" "$USER_TOKEN" \
    '{"date":"2024-12-25T19:30:00","price":85.50,"ownerName":"John Doe","showTitle":"Hamilton","venue":"Broadway Theater"}' \
    "Create ticket (USER)"

test_endpoint "POST" "/api/tickets" "$ADMIN_TOKEN" \
    '{"date":"2024-12-31T20:00:00","price":120.00,"ownerName":"Jane Admin","showTitle":"The Lion King","venue":"Minskoff Theatre"}' \
    "Create ticket (ADMIN)"

# Test getting all tickets
test_endpoint "GET" "/api/tickets" "$USER_TOKEN" "" "Get all tickets (USER)"

# Test getting specific ticket
test_endpoint "GET" "/api/tickets/1" "$USER_TOKEN" "" "Get ticket by ID (USER)"

# Test updating ticket
test_endpoint "PUT" "/api/tickets/1" "$USER_TOKEN" \
    '{"date":"2024-12-25T19:30:00","price":95.00,"ownerName":"John Doe","showTitle":"Hamilton","venue":"Broadway Theater"}' \
    "Update ticket (USER)"

# Test search
test_endpoint "GET" "/api/tickets?showTitle=Hamilton" "$USER_TOKEN" "" "Search tickets by show title (USER)"

# Test delete (should fail for USER)
test_endpoint "DELETE" "/api/tickets/1" "$USER_TOKEN" "" "Delete ticket (USER - should fail)"

# Test delete (should work for ADMIN)
test_endpoint "DELETE" "/api/tickets/1" "$ADMIN_TOKEN" "" "Delete ticket (ADMIN - should work)"

echo ""
echo "✅ API testing completed!"
echo ""
echo "To test manually:"
echo "1. Get access token:"
echo "   curl -X POST '${KEYCLOAK_URL}/realms/ticket-realm/protocol/openid-connect/token' \\"
echo "        -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "        -d 'grant_type=password&client_id=ticket-client&username=user1&password=password123'"
echo ""
echo "2. Use token in API calls:"
echo "   curl -H 'Authorization: Bearer YOUR_TOKEN' '${BASE_URL}/api/tickets'"