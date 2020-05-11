# Bloodhound - The API Fortress Microgateway

## Preamble

Bloodhound is an Open Source, Scala / Akka based, asynchronous, highly modular **API Micro-Gateway**.

Among the capabilities common to most microgateways, Bloodhound has been designed to help developers, QA and data analysts to:

* capture, forward, measure, transform, filter and simulate API calls

Moreover, thanks to the secondary Bloodhound modules, more tasks can be performed, such as:

* allowing access to databases using APIs

* tunnel API calls across datacenters

and more.

Finally, the processing engine can also be fine tuned in great detail to have it perform the way you need.

## Running

We suggest you run Bloodhound using Docker containers. The repository comes with a sample `docker-compose.yml` file and some sample configuration packs.

To get started:

* Download the [docker-compose.yml file](https://github.com/apifortress/Bloodhound/blob/master/docker-compose.yml)

* Download one of the etc.* configuration directories, such as [etc.base](https://github.com/apifortress/Bloodhound/tree/master/etc.base) and rename it to `etc`. Configuration packs come with a `README.md` file describing what each demo endpoint does

* Run the microgateway by issuing `docker-compose up`

## Building

The requirements are:

* Java JDK 8

* Maven 3

Just use Maven to package the software. One big fat JAR will be created:

```text
mvn compile package
```

## Documentation

The documentation of Bloodhound is located in the [doc directory of the repository](https://github.com/apifortress/Bloodhound/tree/master/doc).

## Related projects

* **[Bloodhound Modules](https://github.com/apifortress/afthem-modules)** : official advanced modules

* **[Bloodhound Templates](https://github.com/apifortress/afthem-templates)** : purpose-specific sample configurations

---

Bloodhound is a community project supported by **[API Fortress Inc.](https://apifortress.com)**