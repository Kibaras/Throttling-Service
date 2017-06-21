# Throttling-Service

To start service just write "sbt run"

Yo can setup port and address interface via application.conf parameters:
bindPort = 8080
bindInterface = "0.0.0.0"

http request should be send on slash address '/'
example: http://localhost:8080/

graceRps could be setted up with parameter "graceRps" in application.conf

for benchmarking you can use service:
https://github.com/Kibaras/Throttling-Service-Benchmark.git

i suggest to uncomment 'Random.nextInt(50) + 1' in getSlaByToken method in SlaServiceMock
or there you can setup Sla response more detailed