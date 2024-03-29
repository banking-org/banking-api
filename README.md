# Banking API

## Project configurations

set these following properties in env variable

**DATABASE_URL**: jdbc:postgresql://{HOST}:{PORT}/{DATABASE_NAME}

**DATABASE_USERNAME**: {USERNAME}

**DATABASE_PASSWORD**: *********

**`NB:`** Make sure the database exists

Add a folder `migrations` in src/main/resources/

add or set **addict.mutation** in _application.properties_ or **MUTATE_DB** in env vars to `true` if you want postgres addict introspect your database.

## APIs

[Postman collection](https://www.postman.com/descent-module-geoscientist-81959278/workspace/banking-app/collection/33817658-ea2914df-9fa9-41d1-adf9-3b5d61d02807?action=share&creator=33817658)

## Swagger

[OAS preview](https://petstore.swagger.io/?url=https://raw.githubusercontent.com/banking-org/banking-api/dev/docs/api.yaml)