## iiif-dynamo

An Apache Camel Jetty implementation that queries web annotations with SPARQL and dynamically constructs IIIF manifests.

## Configuration
See `application.properties`

## Building
This requires JDK9 or higher.
To build run
```bash
gradle build
```

## Endpoint
The query endpoint is exposed at `http://localhost:9095/dynamo`

## Example Endpoint Type Query
This example requests canvases with resources that have either metadata value `1676` or metadata value `1670`

```bash
$ http://localhost:9095/dynamic?type=meta&v1=1676&v2=1670
```

## Dependencies
* Start [trellis-compose](https://github.com/trellis-ldp/trellis-deployment/blob/master/trellis-compose/trellis-app/1.9/docker-compose.yml) 

## Resource Aggregation with SPARQL at Fuseki Endpoint
The main use case of this pipeline is to enable repository resources to be grouped as typed collections in named graphs.    

See [Systematik Catalogue Builder](https://github.com/ub-leipzig/systematik-catalogue-builder) for a Web Annotation Builder.
