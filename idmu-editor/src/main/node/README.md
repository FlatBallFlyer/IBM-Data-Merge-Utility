# idmu-editor

## Getting started

```bash
$ sudo yum install -y nodejs npm
$ sudo npm install -g gulp
$ sudo npm install -g bower
$ npm install
$ bower install
$ gulp fonts-copy-init
```

## Package

### Integration

```bash
$ gulp package --integration
```

### Production

```bash
$ gulp package --prod
```

## Update lib (js or css) version

```bash
$ gulp bump-lib-version
```

## Deploy

*  Copy on the target server:

```bash
$ scp -i <your_ssh_key>.pem dist/idmu-editor.zip <your_user>@<host>:/tmp
```

*  Unzip:

```bash
$ unzip -o /tmp/idmu-editor.zip -d /usr/local/idmu-editor
```

## Server and Live Reload

### Server node

```bash
$ gulp server-node
```

### Live Reload

```bash
$ gulp live
```

## Local test

*  run `gulp server node`
*  go to `http://127.0.0.1:9000/idmu`
*  for test error message:

## More

```
$ gulp package --integration && scp dist/idmu-editor.zip username@10.0.0.128:/tmp
$ unzip -o /tmp/idmu-editor.zip -d /usr/local/idmu-editor
```
