#SERVER=http://localhost:8080/api

# Setup the authentication providers
echo "Setting up internal user provider"
curl -H "Content-type:application/json" -d @data/authentication-providers/builtin.json $SERVER/admin/authenticationProviders/

#echo "Setting up Echo providers"
#curl -H "Content-type:application/json" -d @data/authentication-providers/echo.json $SERVER/admin/authenticationProviders/
#curl -H "Content-type:application/json" -d @data/authentication-providers/echo-dignified.json $SERVER/admin/authenticationProviders/
