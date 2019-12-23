IMAGE_NAME=drone-arch
CONTAINER_NAME=main-container

DIRECTORY_PATH=$(shell pwd)

# DOCKER
# -----------------------------------------------------------

# Builds the Docker image
docker-build-image:
	@docker build -t $(IMAGE_NAME) $(DIRECTORY_PATH)

# Builds the Docker image without cache (recommended only for development)
docker-build-image--no-cache:
	@docker build -t $(IMAGE_NAME) $(DIRECTORY_PATH) --no-cache

# Start bash on the container
docker-run-bash:
	@docker run -it --rm --entrypoint=/bin/bash $(IMAGE_NAME)

# Removes the container
docker-rm:
	@docker rm $(CONTAINER_NAME)