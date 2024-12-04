#!/bin/sh

# Ensure the log directory exists
sudo mkdir -p /app/log/

# Set permissions for the log directory
sudo chmod -R 777 /app/log/

# Execute the main command
exec "$@"
