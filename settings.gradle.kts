rootProject.name = "dailyfeed-content-svc"

include(
    "dailyfeed-code",
    "dailyfeed-feign",
    "dailyfeed-content",
    "dailyfeed-pagination-support",
    "dailyfeed-redis-support",
    "dailyfeed-kafka-support",
    "dailyfeed-deadletter-support",
)