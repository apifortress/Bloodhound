# AFtheM - extra actors

## Type: Transformers

### ReplaceUpstreamActor

Replaces the upstream base URL if a certain condition is verified.

**class:** `com.apifortress.afthem.actors.transformers.ReplaceUpstreamActor`

**sidecars:** yes

**config:**

* `expression`: a SpEL expression returning a boolean. The condition to be matched
* `upstream`: the new upstream base URL


### TransformPayloadHeader

Alters a textual payload in a message. If the transformer is placed before an Upstream actor, it modifies the request
payload. If after, it modifies the response payload.

**class:** `com.apifortress.afthem.actors.transformers.TransformPayloadActor`

**sidecars:** yes

**config:**

* `set`: sets the payload with the given value
* `replace`: replaces all the substrings matching a certain regular expression with the provided string. Example:
  ```yaml
    replace:
      regex: foo
      value: bar
  ```

***

## Type: Filters

### ApiKeyFilterActor

Filters out any request that does not carry a valid API key in the headers or in the query string.
This base actor loads the API keys from a YAML file.

When the API key is recognized, the ApiKey object is stored in the `key` meta of the request.

**class:** com.apifortress.afthem.actors.filters.ApiKeyFilterActor

**sidecars**: yes

**config:**

* `filename`: path to a file containing the API keys
* `in`: either `query` (expecting the key in the query string) or `header` (expecting the key in the headers)
* `name`: key of the field carrying the API key

The file format looks like the following:

```yaml
api_keys:
  - api_key: ABC123
    app_id: John Doe
    enabled: true
  - api_key: DEF456
    app_id: Jane Doe
    enabled: true
```

### BasicAuthFilterActor

Filters out any request that does not carry a valid basic authentication header. The valid users are stored
in an htpasswd (md5, apr1) compatible file.

When the authentication succeeds, the username is stored in the `user` meta of the request.

**class:** `com.apifortress.afthem.actors.filters.BasicAuthFilterActor`

**sidecars:** yes

**config:**

* `filename`: path to a htpasswd-compatible file

### ThrottlingActor

Limits the number of requests/second the gateway will accept and pass through. Multiple counting buckets are present.

**class:** `com.apifortress.afthem.actors.filters.ThrottlingActor`

**sidecars:** yes

**config:**

* `global`: (int) the maximum number of requests per second globally for this flow
* `app_id`: (int) maximum number of requests per second per App ID (as defined by API keys)
* `ip_address`: (int) maximum number of requests per second per requesting IP address
