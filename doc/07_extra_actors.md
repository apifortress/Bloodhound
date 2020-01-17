# AFtheM - extra actors

## Type: Filters

### ApiKeyFilterActor

Filters out any request that does not carry a valid API key in the headers or in the query string.
This base actor loads the API keys from a YAML file.

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