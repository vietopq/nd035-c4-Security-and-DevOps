#!/bin/sh

# Ensure the log directory exists
sudo mkdir -p /app/logs

# Set permissions for the log directory
sudo chmod -R 777 /app/logs

# Execute the main command
exec "$@"
