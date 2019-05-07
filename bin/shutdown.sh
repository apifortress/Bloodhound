#!/bin/bash
echo "> Afthem shutting down now"
kill -15 $(ps aux | grep '[a]fthem' | awk '{print $2}')
