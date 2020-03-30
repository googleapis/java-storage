# Changelog

### [1.106.1](https://www.github.com/googleapis/java-storage/compare/v1.106.0...v1.106.1) (2020-03-30)


### Bug Fixes

* storage-client-lib-docs to right location ([#213](https://www.github.com/googleapis/java-storage/issues/213)) ([133d137](https://www.github.com/googleapis/java-storage/commit/133d1377781fd6bdc58dd4f494a75ec1d7b9e530))


### Dependencies

* update conformance test dep ([#210](https://www.github.com/googleapis/java-storage/issues/210)) ([010c112](https://www.github.com/googleapis/java-storage/commit/010c1128761d9c74ba1af33bc34e9264f34b8c80))
* update dependency com.google.api:api-common to v1.9.0 ([#209](https://www.github.com/googleapis/java-storage/issues/209)) ([789ceaa](https://www.github.com/googleapis/java-storage/commit/789ceaa2be6163f85f483637205191e38029e0c2))
* update dependency com.google.cloud.samples:shared-configuration to v1.0.14 ([#207](https://www.github.com/googleapis/java-storage/issues/207)) ([be74072](https://www.github.com/googleapis/java-storage/commit/be74072662f2e3a99e54ee3d3feff66cb39032b2))
* update dependency org.threeten:threetenbp to v1.4.2 ([#200](https://www.github.com/googleapis/java-storage/issues/200)) ([84faad1](https://www.github.com/googleapis/java-storage/commit/84faad1a854c3a189d2997a121a8753988213f90))


### Documentation

* clarify documentation on date formats ([#196](https://www.github.com/googleapis/java-storage/issues/196)) ([9b4af58](https://www.github.com/googleapis/java-storage/commit/9b4af5870ef38cae4e92b60a2f8e6efd3e93d06d)), closes [/github.com/googleapis/google-http-java-client/blob/master/google-http-client/src/main/java/com/google/api/client/util/DateTime.java#L53](https://www.github.com/googleapis//github.com/googleapis/google-http-java-client/blob/master/google-http-client/src/main/java/com/google/api/client/util/DateTime.java/issues/L53)

## [1.106.0](https://www.github.com/googleapis/java-storage/compare/v1.105.2...v1.106.0) (2020-03-17)


### Bug Fixes

* rely on google core for SSLException's ([#188](https://www.github.com/googleapis/java-storage/issues/188)) ([2581f3c](https://www.github.com/googleapis/java-storage/commit/2581f3cfff88ee6a1688ddb881baa30d9967b0c3))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20200226-1.30.9 ([#189](https://www.github.com/googleapis/java-storage/issues/189)) ([b61a820](https://www.github.com/googleapis/java-storage/commit/b61a820a5de4266cfacb76330977962b1940b1e5))

### [1.105.2](https://www.github.com/googleapis/java-storage/compare/v1.105.1...v1.105.2) (2020-03-13)


### Bug Fixes

* connection closed prematurely in BlobReadChannel & ConnectionReset ([#173](https://www.github.com/googleapis/java-storage/issues/173)) ([27bccda](https://www.github.com/googleapis/java-storage/commit/27bccda384da4a7b877b371fbaecc794d6304fbf))


### Dependencies

* update core dependencies ([#171](https://www.github.com/googleapis/java-storage/issues/171)) ([ef5f2c6](https://www.github.com/googleapis/java-storage/commit/ef5f2c6e5079debe8f7f37c3d2c501aac3dc82a6))

### [1.105.1](https://www.github.com/googleapis/java-storage/compare/v1.105.0...v1.105.1) (2020-03-09)


### Bug Fixes

* use %s instead of %d format specifier in checkArgument ([#163](https://www.github.com/googleapis/java-storage/issues/163)) ([ee16197](https://www.github.com/googleapis/java-storage/commit/ee16197d784de167b3ce32eaacbb89d776ce3211))


### Dependencies

* update core dependencies to v1.93.1 ([#161](https://www.github.com/googleapis/java-storage/issues/161)) ([960572f](https://www.github.com/googleapis/java-storage/commit/960572f047ae94e69046b7a59cf9d0e71c6f2dc0))
* update dependency com.google.api-client:google-api-client to v1.30.9 ([#154](https://www.github.com/googleapis/java-storage/issues/154)) ([84dfab9](https://www.github.com/googleapis/java-storage/commit/84dfab9a89d8cbe2c22dd9dea7b05ddcc7b3eb62))
* update dependency com.google.apis:google-api-services-storage to v1-rev20191127-1.30.9 ([#164](https://www.github.com/googleapis/java-storage/issues/164)) ([d9ba7c7](https://www.github.com/googleapis/java-storage/commit/d9ba7c785e280c320a5a65cf3837dbca4c7293b7))
* update dependency com.google.cloud:google-cloud-conformance-tests to v0.0.7 ([#160](https://www.github.com/googleapis/java-storage/issues/160)) ([cbf8082](https://www.github.com/googleapis/java-storage/commit/cbf8082891951966e83315666fd83b58f7ddc0d7))
* update dependency org.apache.httpcomponents:httpclient to v4.5.12 ([#168](https://www.github.com/googleapis/java-storage/issues/168)) ([45b3992](https://www.github.com/googleapis/java-storage/commit/45b39920cfef0c44e2f2ebf1efb94f7502fddd00))

## [1.105.0](https://www.github.com/googleapis/java-storage/compare/v1.104.0...v1.105.0) (2020-02-28)


### Features

* add IAM Conditions support ([#120](https://www.github.com/googleapis/java-storage/issues/120)) ([8256f6d](https://www.github.com/googleapis/java-storage/commit/8256f6d9b479b2fb3c76f887325cb37b051e1654))
* examples of creating a signed url for a blob with generation ([#140](https://www.github.com/googleapis/java-storage/issues/140)) ([420212a](https://www.github.com/googleapis/java-storage/commit/420212a71f675fc1823a7bfdd6a1c5325f17979f))


### Dependencies

* update core dependencies to v1.93.0 ([#153](https://www.github.com/googleapis/java-storage/issues/153)) ([836a2e7](https://www.github.com/googleapis/java-storage/commit/836a2e746011de5f10b28911388b508fef230d84))
* update dependency com.google.api:gax-bom to v1.54.0 ([#152](https://www.github.com/googleapis/java-storage/issues/152)) ([e86051f](https://www.github.com/googleapis/java-storage/commit/e86051f45931269f62c8a372509367cb5e3be009))
* update dependency com.google.cloud:google-cloud-conformance-tests to v0.0.6 ([#151](https://www.github.com/googleapis/java-storage/issues/151)) ([2627a93](https://www.github.com/googleapis/java-storage/commit/2627a938e8b2d295fcd46eebe6b001cbb2ba6784))
* update dependency io.grpc:grpc-bom to v1.27.2 ([e56f8ce](https://www.github.com/googleapis/java-storage/commit/e56f8cefdf7a710b4d74004639af3e4ff086fd1f))

## [1.104.0](https://www.github.com/googleapis/java-storage/compare/v1.103.1...v1.104.0) (2020-02-19)


### Features

* add delimiter BlobListOption ([#102](https://www.github.com/googleapis/java-storage/issues/102)) ([b30a675](https://www.github.com/googleapis/java-storage/commit/b30a6757de84e2ceebc9f28817bcfa5c34c20a30))
* disableGzipContent option on create with InputStream ([#36](https://www.github.com/googleapis/java-storage/issues/36)) ([#82](https://www.github.com/googleapis/java-storage/issues/82)) ([65d3739](https://www.github.com/googleapis/java-storage/commit/65d3739567427e49ca4abfd39702fd4022ee8e3c))


### Bug Fixes

* mismatch chunksize ([#135](https://www.github.com/googleapis/java-storage/issues/135)) ([5da3e8d](https://www.github.com/googleapis/java-storage/commit/5da3e8d3736eed0151e0f564a6d164fb5b429450))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.30.8 ([#111](https://www.github.com/googleapis/java-storage/issues/111)) ([47b1495](https://www.github.com/googleapis/java-storage/commit/47b149509478d211ff103419e695476f42b814f0))
* update dependency com.google.api.grpc:grpc-google-cloud-kms-v1 to v0.83.1 ([#118](https://www.github.com/googleapis/java-storage/issues/118)) ([753d870](https://www.github.com/googleapis/java-storage/commit/753d8700175bdbb2d4c4a51d42399cb400017520))
* update dependency com.google.api.grpc:proto-google-cloud-kms-v1 to v0.83.1 ([#119](https://www.github.com/googleapis/java-storage/issues/119)) ([2c8b9ec](https://www.github.com/googleapis/java-storage/commit/2c8b9ecd527f80397d5921c77aa72bf91fe0bd3c))
* update dependency com.google.http-client:google-http-client-bom to v1.34.2 ([#131](https://www.github.com/googleapis/java-storage/issues/131)) ([fce5b33](https://www.github.com/googleapis/java-storage/commit/fce5b3335bd1d480eb82dcbccf71afc779a1fb25))
* update dependency com.google.protobuf:protobuf-bom to v3.11.3 ([#113](https://www.github.com/googleapis/java-storage/issues/113)) ([044de39](https://www.github.com/googleapis/java-storage/commit/044de393b6523c68eb63c8d1e160288e0c4dc2a0))
* update dependency com.google.protobuf:protobuf-bom to v3.11.4 ([#134](https://www.github.com/googleapis/java-storage/issues/134)) ([1af989e](https://www.github.com/googleapis/java-storage/commit/1af989e1d5745268bfca3d9ffd1ad8e331d94589))
* update dependency io.opencensus:opencensus-api to v0.25.0 ([#129](https://www.github.com/googleapis/java-storage/issues/129)) ([3809576](https://www.github.com/googleapis/java-storage/commit/3809576429a27c13e0c65d986e5306f8aa50bb1a))
* update to gRPC 1.27.0 ([#105](https://www.github.com/googleapis/java-storage/issues/105)) ([64f34bd](https://www.github.com/googleapis/java-storage/commit/64f34bd7a5735aaddecc6a1f76db4f35a320e305))

### [1.103.1](https://www.github.com/googleapis/java-storage/compare/v1.103.0...v1.103.1) (2020-01-27)


### Bug Fixes

* make the getStorageClass() method public ([#22](https://www.github.com/googleapis/java-storage/issues/22)) ([7fb1f6c](https://www.github.com/googleapis/java-storage/commit/7fb1f6c2cb8c5d6ebbf9dcaccf1218d2a0aebb09))


### Dependencies

* update dependency com.google.truth:truth to v1.0.1 ([#60](https://www.github.com/googleapis/java-storage/issues/60)) ([3cedc8f](https://www.github.com/googleapis/java-storage/commit/3cedc8f7fcac0d87ca121197895fc7b36fc8f6d7))
* update dependency org.threeten:threetenbp to v1.4.1 ([4c0f03a](https://www.github.com/googleapis/java-storage/commit/4c0f03a3cc22eed03f002bedf11b3a40e57c709e))

## [1.103.0](https://www.github.com/googleapis/java-storage/compare/1.102.0...v1.103.0) (2020-01-06)


### Features

* add support for archive storage class ([#19](https://www.github.com/googleapis/java-storage/issues/19)) ([a3fbd67](https://www.github.com/googleapis/java-storage/commit/a3fbd67fb0789849922eb7e7b08dc33f3ea9efae))
* make repo releasable ([#3](https://www.github.com/googleapis/java-storage/issues/3)) ([39ff6f6](https://www.github.com/googleapis/java-storage/commit/39ff6f67dc785d3cae070756ca502df749ac9f34))


### Dependencies

* update core transport dependencies ([#16](https://www.github.com/googleapis/java-storage/issues/16)) ([d0a82ab](https://www.github.com/googleapis/java-storage/commit/d0a82ab2b705246923a89a2b826ac1d6d1adba70))
* update dependency com.google.apis:google-api-services-storage to v1-rev20191011-1.30.3 ([#7](https://www.github.com/googleapis/java-storage/issues/7)) ([5ac5b8a](https://www.github.com/googleapis/java-storage/commit/5ac5b8a802e5e6814ba629b0fdb238d3b337756b))
