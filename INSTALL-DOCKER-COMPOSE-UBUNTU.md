sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
/usr/local/bin/docker-compose --version

sudo apt install jq

# To get a User Token:

curl -X POST 'http://localhost:9090/realms/ticket-realm/protocol/openid-connect/token' -H 'Content-Type: application/x-www-form-urlencoded' -d 'grant_type=password&client_id=ticket-client&username=user1&password=password123' | jq -r access_token
