# µService ACL

Le µService ACL implémente plusieurs types d'authentification :

- JWT
- OAuth2

Le choix d'un type d'authentification est libre, quel que soit le cas d'usage.

# JWT

1 entry point :

* `POST /authenticate`

Exemple :

```shell
curl "${host}:${port}/authenticate" \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode "login=rudi" \
  --data-urlencode "password=rudi@123" \
  --verbose
```

Cet appel retourne un json qui ressemble à ceci :

```json
{
	"jwtToken": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJydWRpIiwiY29ubmVjdGVkVXNlciI6eyJsb2dpbiI6InJ1ZGkiLCJ0eXBlIjoiUk9CT1QiLCJmaXJzdG5hbWUiOiJydWRpIiwibGFzdG5hbWUiOiJydWRpIiwiZW1haWwiOm51bGwsIm9yZ2FuaXphdGlvbiI6InJ1ZGkiLCJyb2xlcyI6WyJBRE1JTklTVFJBVE9SIl19LCJleHAiOjE2MzM0NDAyMDEsImlhdCI6MTYzMzQzNjYwMX0.IaHjl2eIRqPhqnH8rSKSANSa7htTHCJvVPNTJ-MOpmVa5xfd1ZMPDzQFavM5KS2RCZZkiBYxG9-GKFwYWu-vew",
	"refreshToken": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJydWRpIiwiY29ubmVjdGVkVXNlciI6eyJsb2dpbiI6InJ1ZGkiLCJ0eXBlIjoiUk9CT1QiLCJmaXJzdG5hbWUiOiJydWRpIiwibGFzdG5hbWUiOiJydWRpIiwiZW1haWwiOm51bGwsIm9yZ2FuaXphdGlvbiI6InJ1ZGkiLCJyb2xlcyI6WyJBRE1JTklTVFJBVE9SIl19LCJleHAiOjE2MzM0NDAyMDEsImlhdCI6MTYzMzQzNjYwMX0.IaHjl2eIRqPhqnH8rSKSANSa7htTHCJvVPNTJ-MOpmVa5xfd1ZMPDzQFavM5KS2RCZZkiBYxG9-GKFwYWu-vew"
}
```

Il est alors possible d'utiliser le jwtToken pour faire des appels au service :

```shell
curl "${host}:${port}/acl/v1/users/me" \
  --header 'Authorization:Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJydWRpIiwiY29ubmVjdGVkVXNlciI6eyJsb2dpbiI6InJ1ZGkiLCJ0eXBlIjoiUk9CT1QiLCJmaXJzdG5hbWUiOiJydWRpIiwibGFzdG5hbWUiOiJydWRpIiwiZW1haWwiOm51bGwsIm9yZ2FuaXphdGlvbiI6InJ1ZGkiLCJyb2xlcyI6WyJBRE1JTklTVFJBVE9SIl19LCJleHAiOjE2MzM0NDAyMDEsImlhdCI6MTYzMzQzNjYwMX0.IaHjl2eIRqPhqnH8rSKSANSa7htTHCJvVPNTJ-MOpmVa5xfd1ZMPDzQFavM5KS2RCZZkiBYxG9-GKFwYWu-vew'
```

# OAuth2

Implémentation du protocole [OAuth2][OAuth2].

3 entry points :

* `POST /oauth/token`
* `GET /oauth/token_key`
* `GET /oauth/check_token`

Plus d'informations sur les paramètres acceptés ici : <https://www.oauth.com/oauth2-servers/authorization/the-authorization-request/>.

## Obtenir un access_token

Requête avec [grant_type=password][password-grant] :

```shell
curl "${host}:${port}/oauth/token" \
    --header 'Authorization: Basic NjA5NGY1YmEtZTAxZC00MTIzLTkyNjYtNzIzNDAwZjVjNTUwOm05ZDhLVHMkS00yV2lQPVZ+L1NKcjVGag==' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=password' \
    --data-urlencode 'username=rudi' \
    --data-urlencode 'password=rudi@123' \
    --data-urlencode 'client_id=rudi' \
    --location --verbose
```

- Le _username_ doit être présent en tant que user dans la base du µService ACL.<br/>
- Le _client_id_ doit être présent en tant que user de type _ROBOT_ dans la base du µService ACL.<br/>
- Le header _Authorization_ donc contenir _base64(<client_id>:<client_password>)_

Requête avec [grant_type=client_credentials][client-credentials] :

```shell
curl "${host}:${port}/oauth/token" \
  --header 'Authorization: Basic NjA5NGY1YmEtZTAxZC00MTIzLTkyNjYtNzIzNDAwZjVjNTUwOm05ZDhLVHMkS00yV2lQPVZ+L1NKcjVGag==' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'grant_type=client_credentials' \
  --location --verbose
```

- Le header _Authorization_ donc contenir _base64(<client_id>:<client_password>)_

Réponse :

```json
{
	"access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzMxMjYyNDEsInVzZXJfbmFtZSI6InJ1ZGkiLCJhdXRob3JpdGllcyI6WyJBRE1JTklTVFJBVE9SIl0sImp0aSI6ImFmOGIzYjQ2LWZjNzgtNDRjMC1iZjBjLTU4YmY0YjJiMTFmYiIsImNsaWVudF9pZCI6InJ1ZGkiLCJzY29wZSI6WyJyZWFkIl19.qBFx4WNUFOftzR4pmR54nlXbSv92DdR7XukNxr34Hx__zpzXxOXpZHS20orqagY8zNRRqyTT5ljmew1V7NLQIn_MsyqQfeJHVOTGo4mtzP-OgyKV0xsy0nYcBiH2YWXWrAYCw2Iqx-CRNVsC2h9lNuhYFk-KbPa39RSzPDiDhf4ZQkwcovLwpO5BsSN9HqJ5xIb_QndDAH1-c4bqGYZ6XvskA3C8IyBoEoQrnS_V0OKK7XyhsF4D5DUss_opUu47R9tdXLc8ugUjsrUakwi40ayPdlyRJre-PTUsLiFHy7BdGHiFzWq8TodcXTj_RqDSW9xahNvoNZIBinJQHVEX56b5khsqmVlvmw6LFJ7injJeP4-bsMGHbFO8C6fGR3NvGdHLaDB37y8eJzRGEyycEeJlNCHScaBbAzAA0vaVThoym-26YO2jpuVeWUd_0uV3TtuH2PQM3zDgmo7tzSY0IkxY06HmR9GbmplCmkLqpgQpmAW_ejoqVx0Rz0VQf_E-",
	"token_type": "bearer",
	"expires_in": 43199,
	"scope": "read write",
	"jti": "af8b3b46-fc78-44c0-bf0c-58bf4b2b11fb"
}
```

## Valider un access token

Requête :

```shell
curl --get "${host}:${port}/oauth/check_token" \
  --data-urlencode 'token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzMxMjYyNDEsInVzZXJfbmFtZSI6InJ1ZGkiLCJhdXRob3JpdGllcyI6WyJBRE1JTklTVFJBVE9SIl0sImp0aSI6ImFmOGIzYjQ2LWZjNzgtNDRjMC1iZjBjLTU4YmY0YjJiMTFmYiIsImNsaWVudF9pZCI6InJ1ZGkiLCJzY29wZSI6WyJyZWFkIl19.qBFx4WNUFOftzR4pmR54nlXbSv92DdR7XukNxr34Hx__zpzXxOXpZHS20orqagY8zNRRqyTT5ljmew1V7NLQIn_MsyqQfeJHVOTGo4mtzP-OgyKV0xsy0nYcBiH2YWXWrAYCw2Iqx-CRNVsC2h9lNuhYFk-KbPa39RSzPDiDhf4ZQkwcovLwpO5BsSN9HqJ5xIb_QndDAH1-c4bqGYZ6XvskA3C8IyBoEoQrnS_V0OKK7XyhsF4D5DUss_opUu47R9tdXLc8ugUjsrUakwi40ayPdlyRJre-PTUsLiFHy7BdGHiFzWq8TodcXTj_RqDSW9xahNvoNZIBinJQHVEX56b5khsqmVlvmw6LFJ7injJeP4-bsMGHbFO8C6fGR3NvGdHLaDB37y8eJzRGEyycEeJlNCHScaBbAzAA0vaVThoym-26YO2jpuVeWUd_0uV3TtuH2PQM3zDgmo7tzSY0IkxY06HmR9GbmplCmkLqpgQpmAW_ejoqVx0Rz0VQf_E-' \
  --location --verbose
```

Réponse :

```json
{
	"user_name": "rudi",
	"scope": [
		"read"
	],
	"active": true,
	"exp": 1633126241,
	"authorities": [
		"ADMINISTRATOR"
	],
	"jti": "af8b3b46-fc78-44c0-bf0c-58bf4b2b11fb",
	"client_id": "rudi"
}
```

## Composants Spring

Tous les composants se trouvent dans le package `org.springframework.security.oauth2.provider.endpoint`.

* `/oauth/token` → **TokenEndpoint**
* `/oauth/token_key` → **TokenKeyEndpoint**
* `/oauth/check_token` → **CheckTokenEndpoint**

# Voir aussi

- [GitHub Rennes Métropole][GitHub Rennes Métropole]


[OAuth2]: https://www.oauth.com/
[password-grant]: https://www.oauth.com/oauth2-servers/access-tokens/password-grant/
[client-credentials]: https://www.oauth.com/oauth2-servers/access-tokens/client-credentials/
[GitHub Rennes Métropole]: https://github.com/sigrennesmetropole/rudi_documentation/tree/main/authentification
