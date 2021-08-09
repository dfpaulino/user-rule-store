# user-rule-store

## What is it

The goal of this artifact is to cache the applied/eligible rules for a user. This will manage the lifecycle of the
element (delete when it reaches the TTL)
The storage will be a sql database (mysql/H2). We could go with mongoDb

Provides a reactive REST CRUD API :

* add rule information
* delete rule information
* get rule information by name
* get all Rules
* update ttl for a specific rule

Note : TBD how to identify the rule