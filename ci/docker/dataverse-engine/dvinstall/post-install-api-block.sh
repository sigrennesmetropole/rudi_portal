#!/bin/bash

# This script can be run on a system that was set up with unrestricted access to 
# the sensitive API endpoints, in order to block it for the general public.

# First, revoke the authentication token from the built-in user: 
curl -X DELETE $SERVER/admin/settings/BuiltinUsers.KEY

# Block the sensitive endpoints:
# Relevant settings:
#  - :BlockedApiPolicy - one of allow, drop, localhost-only, unblock-key
#  - :BlockedApiKey - when using the unblock-key policy, pass this key in the unblock-key query param to allow the call to a blocked endpoint
#  - :BlockedApiEndpoints - comma separated list of blocked api endpoints

# This leaves /api/admin and /api/test blocked to all connections except from those 
# coming from localhost:
curl -X PUT -d localhost-only $SERVER/admin/settings/:BlockedApiPolicy
curl -X PUT -d admin,test $SERVER/admin/settings/:BlockedApiEndpoints

# In some situations, you may prefer an alternative solution - to block ALL connections to 
# these endpoints completely; but allow connections authenticated with the defined 
# "unblock key" (password): 

#curl -X PUT -d YOURSUPERSECRETUNBLOCKKEY $SERVER/admin/settings/:BlockedApiKey 
#curl -X PUT -d unblock-key $SERVER/admin/settings/:BlockedApiPolicy


