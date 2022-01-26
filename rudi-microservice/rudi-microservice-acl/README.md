# µService ACL

# JWT

Le µService ACL implémente une authentification JWT pour les utilisateurs.

1 entry point :
* &lt;>/authenticate (POST) 

Exemple :
<pre>
curl -v -H "Content-Type: application/x-www-form-urlencoded" -X POST http://localhost:8086/authenticate --data "login=fnisseron" --data "password=fnisseron@123"
</pre>

Cet appel retourne un json qui ressemble à ceci :
<pre>
{"jwtToken":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmbmlzc2Vyb24iLCJjb25uZWN0ZWRVc2VyIjp7ImxvZ2luIjoiZm5pc3Nlcm9uIiwidHlwZSI6IlBFUlNPTiIsImZpcnN0bmFtZSI6ImZsb3JlbnQiLCJsYXN0bmFtZSI6Im5pc3Nlcm9uIiwiZW1haWwiOm51bGwsIm9yZ2FuaXphdGlvbiI6Im9wZW4iLCJyb2xlcyI6WyJBRE1JTklTVFJBVE9SIl19LCJleHAiOjE2MTMwNTc2MTAsImlhdCI6MTYxMzA1NDAxMH0.7BYLzs8V5undnZ4gYZcfwq0cv6lxMvDAbZovJWIHC6aquzaTifF3lLtp0Yh8jX9y5hGiMrIaft_cUnFTWAILLw","refreshToken":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmbmlzc2Vyb24iLCJjb25uZWN0ZWRVc2VyIjp7ImxvZ2luIjoiZm5pc3Nlcm9uIiwidHlwZSI6IlBFUlNPTiIsImZpcnN0bmFtZSI6ImZsb3JlbnQiLCJsYXN0bmFtZSI6Im5pc3Nlcm9uIiwiZW1haWwiOm51bGwsIm9yZ2FuaXphdGlvbiI6Im9wZW4iLCJyb2xlcyI6WyJBRE1JTklTVFJBVE9SIl19LCJleHAiOjE2MTMwNTc2NTcsImlhdCI6MTYxMzA1NDA1N30.O0ozFx87FSXGQl28JDA4Bplgqd-EfpIDYoIPDG3U4rsAWHgjtqXG-GV1SahmxDsBolqA6MuWgIaTcXVUgR9elg"}
</pre>

Il est alors possible d'utiliser le jwtToken pour faire des appesl au service:

<pre>
curl -X GET http://localhost:8086/acl/users -H "Content-type:application/json" -H "Authorization:Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmbmlzc2Vyb24iLCJjb25uZWN0ZWRVc2VyIjp7ImxvZ2luIjoiZm5pc3Nlcm9uIiwidHlwZSI6IlBFUlNPTiIsImZpcnN0bmFtZSI6ImZsb3JlbnQiLCJsYXN0bmFtZSI6Im5pc3Nlcm9uIiwiZW1haWwiOm51bGwsIm9yZ2FuaXphdGlvbiI6Im9wZW4iLCJyb2xlcyI6WyJBRE1JTklTVFJBVE9SIl19LCJleHAiOjE2MTMwNTc2MTAsImlhdCI6MTYxMzA1NDAxMH0.7BYLzs8V5undnZ4gYZcfwq0cv6lxMvDAbZovJWIHC6aquzaTifF3lLtp0Yh8jX9y5hGiMrIaft_cUnFTWAILLw"
</pre>

# OAuth2

Le µService ACL implémente le protocole OAuth2 pour l'authentification des tierces parties applicatives.

3 entry points :
* &lt;>/oauth/token
* &lt;>/oauth/token_key
* &lt;>/oauth/check_token

Pour obtenir un acces token, cela correspond à l'appel suivant :
<pre>
curl -v --request POST http://&lt;server>:&lt;port>/oauth/token --data "grant_type=password" --data "username=&lt;username>" --data "password=&lt;usernamepassword>" --data "scope=read" --data "client_id=rudi" -H "Authorization:Basic cnVkaTpydWRpQDEyMw=="
</pre>

Le  _username_  doit être présent en tant que user dans la base du µService ACL.<br/>
Le  _client_id_  doit être présent en tant que user de type  _ROBOT_  dans la base du µService ACL.<br/>
Le header  _Authorization_  donc contenir  _base64(<client_id>:<client_password>)_

Pour valider un access token, cela correspond à l'appel suivant :
<pre>
curl -v --request GET http://&lt;server>:&lt;port>/oauth/check_token?token=<token>
</pre>
