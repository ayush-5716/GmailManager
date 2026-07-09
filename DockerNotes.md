<!-- prettier-ignore -->
# Docker Notes

## Lecture 1 -----------------------------------------------------------------------

## Content

- `Dockerfile` is the special file Docker reads to build an image.
- Put it in the project folder that contains your app code.
- `FROM node` starts the image from the Node base image.
- Docker pulls it from Docker Hub if it is not already local.
- `COPY . /app` copies the current project folder into `/app` inside the image.
- `WORKDIR /app` sets `/app` as the folder for later commands.
- `RUN npm install` installs dependencies during image build.
- `CMD ["node", "server.js"]` starts the server when a container runs.
- Use `CMD` for runtime startup, not `RUN`.
- `EXPOSE 80` tells Docker that the container uses port 80.
- It helps make the container port available from outside.
- `FROM`, `COPY`, `WORKDIR`, `RUN`, `EXPOSE`, and `CMD` are the core Dockerfile instructions here.
- `RUN` happens during image build.
- `CMD` happens when a container starts.
- Keep `CMD` as the last instruction.
- Use a clear app folder like `/app`.

## Mistakes

- If the server starts too early, you used `RUN` instead of `CMD`.
- If the app is unreachable, check that the port is exposed.
- If commands fail, verify that `WORKDIR` is set correctly.

## Lecture 2 ---------------------------------------------------------------------------

## Content

### `docker build .`

- Builds a custom image from the `Dockerfile`.
- The `.` means Docker should look for the `Dockerfile` in the current folder.
- Docker executes the instructions in the `Dockerfile` step by step.
- Typical build steps are `FROM`, `WORKDIR`, `COPY`, `RUN npm install`, `EXPOSE`, and `CMD`.
- When the build is done, Docker gives you an image ID.

### `docker run -p 3000:80 <image-id>`

- Starts a container from the image.
- The command runs the Node server inside the container.
- The container stays running because the server process does not exit.
- `-p` publishes a port from the container to your machine.
- In `3000:80`, `3000` is the local port and `80` is the container port.
- After mapping the port, you can open `localhost:3000` in the browser.

### `docker ps`

- Shows all running containers.
- Use it to find the container name if you want to stop it.

### `docker stop <container-name>`

- Stops a running container.
- This also stops the Node server running inside the container.

### `docker ps -a`

- Shows all containers, including stopped ones.
- Useful if you want to confirm that a container exited.

## NOTE:

- `EXPOSE 80` is mainly for documentation.
- `EXPOSE` does not publish the port to your host machine by itself.
- To access the app from the browser, you still need `docker run -p <local-port>:<container-port>`.
- The usual flow is: build the image, run the container, then stop it when you are done.

## Lecture 3 ---------------------------------------------------------------------------

## Content

- The code copied into an image is a snapshot from build time.
- If you change the source code after building, the existing image does not change.
- To include updated code, rebuild the image and then run a container from the new image.
- The new image gets a new image ID because it is a new build.

## NOTE:

- An image is a snapshot of the code at build time.
- After the image is built, it is effectively read only.
- Changing files on your machine does not change an already built image.
- If you update copied source code, you need to build the image again.
- Rebuilding is required to pick up external code changes.

## Lecture 4 ---------------------------------------------------------------------------

### Managin images and containers

-> Show all running containers
| docker ps

-> Show all the container (running or not running )
| docker ps -a

-> To restart a already running container - 1. list all the available containers -
| docker ps -a 2. copy the <container-name> that we want to restart then use -
| docker start <container-name>

    --> Once a container is stopped, its state is saved as it is , with all the data and everything,
        Then once restarted, it starts with the same state.

## Lecture 5 ---------------------------------------------------------------------------

### Attacehd and detached containers

-> When running docker start <container-name>, this runs the container in detached mode.
-> docker run -> runs default in attached mode.

-> Attached mode - Means that from our current terminal we are listening to the output of that container.

-> To run a new container in attached mode -
| docker run -p 8080:8080 <image-id> --> normal commmand , no change

-> To run a new container in detached mode -
| docker run -p 8080:8080 -d <image-id>

-> To restart a already created, but down container in attached mode -
| docker start -a <container-name>

-> To attach to a detached running container -
| docker attach <container-name>  
 or
| docker logs <container-name> --> This will just display the recent logs by the container.
or
| docker logs <container-name> -f --> -f stands for follow, which means you will see the logs and follow it.

## Lecture 6 ---------------------------------------------------------------------------

### Interfactive terminal

-> For example , we have a python script that takes input from the use through command line.
| int(input("enter age:")) -->python code

-> Here, if we run the container normally using docker run <image-id> , we will get errors.
-> This is because , even in attached mode we cannot enter or interact with the terminal .

-> to enable giving input or interact with the terminal , we use -i and -t flag.
| docker run -i -t <container-id>
-> -i stands for interactive and -t stands for create temporary terminal
-> so -t creates a temporary terminal for the applicaton , and -i allows us to interact with it

NOTE:
-> To interact with a shut down continer, which was originally started with -i -t flag, we use
docker start -i <container-name>

## Lecture 7 ---------------------------------------------------------------------------

### deleting images and containers

-> To remove multiple containers -
| docker rm <container1-name> <container2-name> --> just all the container names need to be space separated

-> To remove images -
| docker rmi <image-id> <image1-id> --> again space separated
NOTE : Images can be removed only when any container (running or stopped) does not exists based on this image

-> To remove all images that do not have any container for them -
| docker image prune

--> To make a container delete itself, once the container comes down -
| docker run --rm <image-id> --> once we stop the container, it will be completely deleted

## Lecture 8 ---------------------------------------------------------------------------
