# user-rule-store
[![CircleCI](https://circleci.com/gh/dfpaulino/user-rule-store/tree/main.svg?style=shield)](https://circleci.com/gh/dfpaulino/user-rule-store/tree/main)
## What is it

The goal of this artifact is to cache the applied/eligible rules for a user. This will manage the lifecycle of the
element (delete when it reaches the TTL)
The storage will be a No sql database (mongoDb).

Provides a reactive REST CRUD API :

* add rule information
* delete rule information
* get rule information by name
* get all Rules
* update ttl for a specific rule

Note : TBD how to identify the rule