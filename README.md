# Maestro

Maestro is a Core Java Application of the Gazelle test bed. It is responsible for executing automated tests by reaching the correct services.

## Requirements

- JDK 21+/Maven 3.8.8+
- Docker 27.0.2+
- Docker-compose 2.18.+

## Documentation

- [Features](documentation/features.md)

## Build artifacts

It is possible to generate the artifacts with maven.

```bash
mvn clean package
```

This command will directly build the docker image of the project :
- rg.fr-par.scw.cloud/gazelle-snapshot/app/maestro:${project.version}

## Testing

It is possible to run different types of test in this application :

- Unit tests that are run with the following command, the jacoco results can be found in
  module_path/target/site/jacoco/

```bash
mvn clean test
```

- Integration tests that can be run with the following command

```bash
mvn verify
```

- Mutation tests that can be run with the following command, the results can be found in
  module_path/target/pit-reports/

  In keycloak-provider module, the mutation tests are skipped.

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

## License

```
    Copyright 2022-2025 IHE International
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

