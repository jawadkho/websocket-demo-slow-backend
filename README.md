# WebSockets Queue Service Demo

A websockets demo showing how a webpage can be actively updated by a server which calls a throttled/slow backend service (in this case called a Queue service).

## Setup

This project requries Gradle and NPM.

Install Node dependencies:

```bash
cd webapp
npm install -y
```

## Run

Start the java backend server:

```bash
gradle run
```

Start the node frontend server:

```bash
cd webapp
npm start
```
