#!/usr/bin/env bash

set -e

readonly run_cmd="/opt/mssql-tools/bin/sqlcmd -U sa -P 'root!R00t!' -l 30 -i create-db.sql"

>&2 echo "Allowing 30 seconds for SQL Server to bootstrap, then creating database.."

until $run_cmd
do
	>&2 echo "This should not be executing!"
done