# Changelog

## [2.8.1](https://github.com/googleapis/java-storage/compare/v2.8.0...v2.8.1) (2022-06-13)


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20220608-1.32.1 ([#1448](https://github.com/googleapis/java-storage/issues/1448)) ([96676cd](https://github.com/googleapis/java-storage/commit/96676cd830aca27c23c08e02e8cc7c58dece686c))

## [2.8.0](https://github.com/googleapis/java-storage/compare/v2.7.2...v2.8.0) (2022-06-08)


### Features

* Prefix/Suffix Matches Lifecycle Condition ([#1389](https://github.com/googleapis/java-storage/issues/1389)) ([20c8848](https://github.com/googleapis/java-storage/commit/20c88489d80d716da28f78fed628b54345f32ca4))
* Support AbortIncompleteMultipartUpload LifecycleAction ([#1347](https://github.com/googleapis/java-storage/issues/1347)) ([7c3aba2](https://github.com/googleapis/java-storage/commit/7c3aba2f0a26ac550e4f37f9287ed6b041d75919))


### Bug Fixes

* update request method of HttpStorageRpc to properly configure offset on requests ([#1434](https://github.com/googleapis/java-storage/issues/1434)) ([72dc0df](https://github.com/googleapis/java-storage/commit/72dc0dff20d76875401dac721c0268c32e475e39))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20220604-1.32.1 ([#1438](https://github.com/googleapis/java-storage/issues/1438)) ([df8fcd9](https://github.com/googleapis/java-storage/commit/df8fcd9925ef06c91ebebe0a3a7b5aedeb15ec4d))
* update dependency com.google.cloud:google-cloud-pubsub to v1.119.0 ([#1426](https://github.com/googleapis/java-storage/issues/1426)) ([93ba28c](https://github.com/googleapis/java-storage/commit/93ba28cff16d428e0222078dc60dbf49fda7632a))

### [2.7.2](https://github.com/googleapis/java-storage/compare/v2.7.1...v2.7.2) (2022-05-27)


### Dependencies

* update kms.version to v0.96.1 ([#1418](https://github.com/googleapis/java-storage/issues/1418)) ([d2f325b](https://github.com/googleapis/java-storage/commit/d2f325b3d31ac5da367873be1fb530fb6356036a))

### [2.7.1](https://github.com/googleapis/java-storage/compare/v2.7.0...v2.7.1) (2022-05-24)


### Dependencies

* update kms.version to v0.96.0 ([#1408](https://github.com/googleapis/java-storage/issues/1408)) ([7501ffc](https://github.com/googleapis/java-storage/commit/7501ffc97d5a7943d3852ea26133b6c62cbbff1f))

## [2.7.0](https://github.com/googleapis/java-storage/compare/v2.6.1...v2.7.0) (2022-05-24)


### Features

* add build scripts for native image testing in Java 17 ([#1440](https://github.com/googleapis/java-storage/issues/1440)) ([#1400](https://github.com/googleapis/java-storage/issues/1400)) ([274a373](https://github.com/googleapis/java-storage/commit/274a3733b72d2aa1e2916edf40a72c013aaf1711))
* add Storage#downloadTo ([#1354](https://github.com/googleapis/java-storage/issues/1354)) ([5a565a7](https://github.com/googleapis/java-storage/commit/5a565a74cd6aaa85ed81a8cea026477512fbd5da))
* change GCS gRPC API to get user billing project from gRPC metadata instead of CommonRequestParams, and remove latter ([#1396](https://github.com/googleapis/java-storage/issues/1396)) ([8a7755c](https://github.com/googleapis/java-storage/commit/8a7755cc8352b3ab21c252885fb86576474d7f09))


### Documentation

* add new storage_download_byte_range samples ([#1325](https://github.com/googleapis/java-storage/issues/1325)) ([cef3d13](https://github.com/googleapis/java-storage/commit/cef3d138fd11762437ac59adee6a198139acb7f5))
* **sample:** removing unnecessary native-image-support dependency ([#1373](https://github.com/googleapis/java-storage/issues/1373)) ([3a246ef](https://github.com/googleapis/java-storage/commit/3a246ef4f0a75e52734df52772d34547632ab85f))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20220509-1.32.1 ([#1386](https://github.com/googleapis/java-storage/issues/1386)) ([4e93c8e](https://github.com/googleapis/java-storage/commit/4e93c8e6f3c8259968a3dd35a15e752a81491af2))
* update dependency com.google.cloud:google-cloud-pubsub to v1.116.4 ([#1360](https://github.com/googleapis/java-storage/issues/1360)) ([66c7ffe](https://github.com/googleapis/java-storage/commit/66c7ffe112242915165286a972c44fc2568b67c8))
* update dependency com.google.cloud:google-cloud-pubsub to v1.117.0 ([#1382](https://github.com/googleapis/java-storage/issues/1382)) ([0cd01a0](https://github.com/googleapis/java-storage/commit/0cd01a0eb498a994c330cc985c21b3248ecba8fa))
* update dependency com.google.cloud:google-cloud-pubsub to v1.118.0 ([#1397](https://github.com/googleapis/java-storage/issues/1397)) ([fc0c187](https://github.com/googleapis/java-storage/commit/fc0c187096058f84a2f73704b29457c5c6d744fe))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.11.0 ([#1398](https://github.com/googleapis/java-storage/issues/1398)) ([8834423](https://github.com/googleapis/java-storage/commit/8834423f8772310b1a99aa393095e319a4169307))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.12.0 ([#1402](https://github.com/googleapis/java-storage/issues/1402)) ([32cded4](https://github.com/googleapis/java-storage/commit/32cded493442ed5e7b524cd2478e97f00fd90c3c))
* update kms.version to v0.95.4 ([#1361](https://github.com/googleapis/java-storage/issues/1361)) ([2f42ba2](https://github.com/googleapis/java-storage/commit/2f42ba296bf5ace92159ee02885eaf0e4d9c9864))

### [2.6.1](https://github.com/googleapis/java-storage/compare/v2.6.0...v2.6.1) (2022-04-15)


### Bug Fixes

* add gccl-invocation-id interceptor ([#1309](https://github.com/googleapis/java-storage/issues/1309)) ([335c267](https://github.com/googleapis/java-storage/commit/335c2679b70f0bcd4db895d9cb4cbe97175e8070))
* **java:** add service account email to Native Image testing kokoro job ([#1348](https://github.com/googleapis/java-storage/issues/1348)) ([9f76fcc](https://github.com/googleapis/java-storage/commit/9f76fccfddcc0d3a671ec4281dab303da07b9959))


### Documentation

* Adding PubSub Notification Samples ([#1317](https://github.com/googleapis/java-storage/issues/1317)) ([fa9920d](https://github.com/googleapis/java-storage/commit/fa9920d9097cfe6863c3e733a091c9b867d603ef))
* Adding Samples for Creating Dual Region Buckets ([#1341](https://github.com/googleapis/java-storage/issues/1341)) ([9396061](https://github.com/googleapis/java-storage/commit/9396061ac71f98efd6784c34da3bbea04f48873d))
* Update CreateBucketPubSubNotification to have line without horizontal scrolling ([#1335](https://github.com/googleapis/java-storage/issues/1335)) ([09b7842](https://github.com/googleapis/java-storage/commit/09b78424f7090c7c0469709a357a06143668e31b))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20220401-1.32.1 ([#1337](https://github.com/googleapis/java-storage/issues/1337)) ([a5050e2](https://github.com/googleapis/java-storage/commit/a5050e230a620ba727a93c0a93f0bf82a011ce99))
* update dependency com.google.cloud:google-cloud-pubsub to v1.116.3 ([#1327](https://github.com/googleapis/java-storage/issues/1327)) ([9d8c520](https://github.com/googleapis/java-storage/commit/9d8c520acca7f56f5af46348bc1db71bda1f93aa))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.10.0 ([#1352](https://github.com/googleapis/java-storage/issues/1352)) ([ab46f98](https://github.com/googleapis/java-storage/commit/ab46f985768c1539babf4c14a7e030083776ce0e))
* update dependency com.google.cloud:native-image-support to v0.12.11 ([#1319](https://github.com/googleapis/java-storage/issues/1319)) ([c338c54](https://github.com/googleapis/java-storage/commit/c338c54210940dbe3b97aa0e7b13904e72ede91d))
* update dependency com.google.cloud:native-image-support to v0.13.1 ([#1353](https://github.com/googleapis/java-storage/issues/1353)) ([0f76d27](https://github.com/googleapis/java-storage/commit/0f76d2773bc159b1c8a9eeddb54ae8406da86e7a))
* update kms.version to v0.95.2 ([#1331](https://github.com/googleapis/java-storage/issues/1331)) ([2ca4883](https://github.com/googleapis/java-storage/commit/2ca488362ce2cb2b620ed6dc846d76b095d44a31))
* update kms.version to v0.95.3 ([#1346](https://github.com/googleapis/java-storage/issues/1346)) ([a4f9503](https://github.com/googleapis/java-storage/commit/a4f95038e56ac36badf68edd072705621fec1dbe))

## [2.6.0](https://github.com/googleapis/java-storage/compare/v2.5.1...v2.6.0) (2022-03-30)


### Features

* replace enum with string representation for predefined ACLs and public_access_prevention ([#1323](https://github.com/googleapis/java-storage/issues/1323)) ([4dd1a88](https://github.com/googleapis/java-storage/commit/4dd1a8800317343bb0cd575864683e580f9ccd29))


### Bug Fixes

* **java:** add configurations for Storage tests ([#1305](https://github.com/googleapis/java-storage/issues/1305)) ([2bacf92](https://github.com/googleapis/java-storage/commit/2bacf92799e8a0fbdc1b5cfcfc6ef8d806a53fa3))
* update boundary checking of BlobReadChannel when limit() is used ([#1324](https://github.com/googleapis/java-storage/issues/1324)) ([f21f624](https://github.com/googleapis/java-storage/commit/f21f624f1645b5ada350c04c774f9f113e76e971))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.9.0 ([#1321](https://github.com/googleapis/java-storage/issues/1321)) ([f48d8dd](https://github.com/googleapis/java-storage/commit/f48d8dd09e918ba4a54fccaebf65feaba4f6e206))


### Documentation

* adjust retry settings for hmac samples ([#1303](https://github.com/googleapis/java-storage/issues/1303)) ([d0c5361](https://github.com/googleapis/java-storage/commit/d0c5361e9e4996f8a99754381e5a28a843e6de2a))

### [2.5.1](https://github.com/googleapis/java-storage/compare/v2.5.0...v2.5.1) (2022-03-28)


### Dependencies

* update dependency com.google.cloud:google-cloud-pubsub to v1.116.2 ([#1310](https://github.com/googleapis/java-storage/issues/1310)) ([fb64493](https://github.com/googleapis/java-storage/commit/fb644932d4350b4e33481abda8cc8f498f9da85e))

## [2.5.0](https://github.com/googleapis/java-storage/compare/v2.4.5...v2.5.0) (2022-03-25)


### Features

* allow limiting ReadChannel ([#1180](https://github.com/googleapis/java-storage/issues/1180)) ([2898ee8](https://github.com/googleapis/java-storage/commit/2898ee88545a93916d55c969fd0838e4fc703912))
* expose the methods of Notifications ([#399](https://github.com/googleapis/java-storage/issues/399)) ([0bd17b1](https://github.com/googleapis/java-storage/commit/0bd17b1f70e47081941a44f018e3098b37ba2c47))


### Documentation

* Adding Samples for printing all Acls for a file and for a specific user ([#1288](https://github.com/googleapis/java-storage/issues/1288)) ([32fe388](https://github.com/googleapis/java-storage/commit/32fe388c8733cb237fc2a5b4676e36df76ef0dff))
* Copy all storage samples from java-docs-samples ([#1258](https://github.com/googleapis/java-storage/issues/1258)) ([48b99be](https://github.com/googleapis/java-storage/commit/48b99beb692f529bea7e3de7ea5a36606876d96a))
* **sample:** Add Native Image sample for Storage ([#1283](https://github.com/googleapis/java-storage/issues/1283)) ([375874d](https://github.com/googleapis/java-storage/commit/375874d38fc46bfec2df4c58e7d661c4f1f6c486))


### Dependencies

* allow snapshot to update properly ([#1311](https://github.com/googleapis/java-storage/issues/1311)) ([a5d32f2](https://github.com/googleapis/java-storage/commit/a5d32f2945247f76a21b030300a6e037084231b5))
* update dependency com.google.cloud:native-image-support to v0.12.10 ([#1295](https://github.com/googleapis/java-storage/issues/1295)) ([3b3ecce](https://github.com/googleapis/java-storage/commit/3b3ecce262a3c7c95fbf0ddf3a5830a116022053))
* update dependency org.graalvm.buildtools:junit-platform-native to v0.9.10 ([#1296](https://github.com/googleapis/java-storage/issues/1296)) ([6f1b142](https://github.com/googleapis/java-storage/commit/6f1b1423d1de6aef9aedbf6b89ce42bbc72196e9))
* update dependency org.graalvm.buildtools:junit-platform-native to v0.9.11 ([#1306](https://github.com/googleapis/java-storage/issues/1306)) ([1527ba0](https://github.com/googleapis/java-storage/commit/1527ba0abad38acd55542ce92214d5c66a9c62ee))
* update dependency org.graalvm.buildtools:native-maven-plugin to v0.9.10 ([#1297](https://github.com/googleapis/java-storage/issues/1297)) ([3f64f11](https://github.com/googleapis/java-storage/commit/3f64f117be7b7150a7a89c5240f09350d1add578))
* update dependency org.graalvm.buildtools:native-maven-plugin to v0.9.11 ([#1307](https://github.com/googleapis/java-storage/issues/1307)) ([e45ae07](https://github.com/googleapis/java-storage/commit/e45ae0783bd9e0bea7e8accee1437dee4e974333))

### [2.4.5](https://github.com/googleapis/java-storage/compare/v2.4.4...v2.4.5) (2022-03-04)


### Documentation

* Adding Samples for Adding/Removing File Owners ([#1273](https://github.com/googleapis/java-storage/issues/1273)) ([6fad19c](https://github.com/googleapis/java-storage/commit/6fad19c184d108f30c85f62426d254a9f0ff715d))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.8.0 ([#1284](https://github.com/googleapis/java-storage/issues/1284)) ([0f71ae4](https://github.com/googleapis/java-storage/commit/0f71ae41fbabf6a3f38674a2f68fb55bd9809595))
* update kms.version to v0.95.1 ([#1287](https://github.com/googleapis/java-storage/issues/1287)) ([8334d3c](https://github.com/googleapis/java-storage/commit/8334d3cb1f527b00ee8f19583dcf112f4f1b08ac))

### [2.4.4](https://github.com/googleapis/java-storage/compare/v2.4.3...v2.4.4) (2022-02-28)


### Dependencies

* update actions/setup-java action to v3 ([#1274](https://github.com/googleapis/java-storage/issues/1274)) ([d29d19a](https://github.com/googleapis/java-storage/commit/d29d19a9936164e0ffe4d2f5fa14739a807369f6))

### [2.4.3](https://github.com/googleapis/java-storage/compare/v2.4.2...v2.4.3) (2022-02-25)


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20220210-1.32.1 ([#1269](https://github.com/googleapis/java-storage/issues/1269)) ([b3933be](https://github.com/googleapis/java-storage/commit/b3933be393bcb8850e39635d28211159a3d0a091))


### Documentation

* Adding Samples for Add/Remove Bucket Default Owner ([#1260](https://github.com/googleapis/java-storage/issues/1260)) ([7223626](https://github.com/googleapis/java-storage/commit/7223626481930bf4442a04ccf49536f7f9e5fd32))
* Adding Samples for Add/Remove Bucket Owner ([#1272](https://github.com/googleapis/java-storage/issues/1272)) ([9d25fa9](https://github.com/googleapis/java-storage/commit/9d25fa986ec6116eeb16ac5773b46e7fdbe10647))
* Adding Samples for Printing Bucket ACLs and Printing Bucket ACL for a specific user. ([#1236](https://github.com/googleapis/java-storage/issues/1236)) ([d82333b](https://github.com/googleapis/java-storage/commit/d82333b01eadd9afd0c9d58455f86bc6457c99e3))

### [2.4.2](https://github.com/googleapis/java-storage/compare/v2.4.1...v2.4.2) (2022-02-11)


### Dependencies

* update actions/github-script action to v6 ([#1241](https://github.com/googleapis/java-storage/issues/1241)) ([366d738](https://github.com/googleapis/java-storage/commit/366d7385c4f6ac5c7478ea71cf0f7f1546ad4607))

### [2.4.1](https://github.com/googleapis/java-storage/compare/v2.4.0...v2.4.1) (2022-02-08)


### Dependencies

* update kms.version to v0.95.0 ([#1224](https://github.com/googleapis/java-storage/issues/1224)) ([5700c54](https://github.com/googleapis/java-storage/commit/5700c544da904bca75bf42314b150f109771f719))

## [2.4.0](https://github.com/googleapis/java-storage/compare/v2.3.0...v2.4.0) (2022-02-03)


### Features

* Change RewriteObjectRequest to specify bucket name, object name and KMS key outside of Object resource ([#1218](https://github.com/googleapis/java-storage/issues/1218)) ([8789e4f](https://github.com/googleapis/java-storage/commit/8789e4f73a3c5b36aa93246d172d07adb24027aa))
* re-generate gapic client to include full GCS gRPC API ([#1189](https://github.com/googleapis/java-storage/issues/1189)) ([3099a22](https://github.com/googleapis/java-storage/commit/3099a2264d8b135f602d8dd06f3e91ac5b0ecdba))
* Update definition of RewriteObjectRequest to bring to parity with JSON API support ([#1220](https://github.com/googleapis/java-storage/issues/1220)) ([7845c0e](https://github.com/googleapis/java-storage/commit/7845c0e8be5ba150f5e835172e9341ef2efc6054))


### Bug Fixes

* Remove post policy v4 client side validation ([#1210](https://github.com/googleapis/java-storage/issues/1210)) ([631741d](https://github.com/googleapis/java-storage/commit/631741df96a6dddd31a38dce099f3d3ff09ca7cf))


### Dependencies

* **java:** update actions/github-script action to v5 ([#1339](https://github.com/googleapis/java-storage/issues/1339)) ([#1215](https://github.com/googleapis/java-storage/issues/1215)) ([deb110b](https://github.com/googleapis/java-storage/commit/deb110b0b5ec4a7e6963d1c1ab0e63ca58240ae1))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.7.0 ([#1219](https://github.com/googleapis/java-storage/issues/1219)) ([623e68b](https://github.com/googleapis/java-storage/commit/623e68b8b678df425730b6472cf34d7b78841757))

## [2.3.0](https://github.com/googleapis/java-storage/compare/v2.2.3...v2.3.0) (2022-01-12)


### Features

* Add RPO metadata settings ([#1105](https://github.com/googleapis/java-storage/issues/1105)) ([6f9dfdf](https://github.com/googleapis/java-storage/commit/6f9dfdfdbf9f1466839a17ef97489f207f18bec6))


### Bug Fixes

* **java:** run Maven in plain console-friendly mode ([#1301](https://github.com/googleapis/java-storage/issues/1301)) ([#1186](https://github.com/googleapis/java-storage/issues/1186)) ([1e55dba](https://github.com/googleapis/java-storage/commit/1e55dba4cd5111472b9bb05db08ba7e47fafe762))
* Remove all client side validation for OLM, allow nonspecific lif… ([#1160](https://github.com/googleapis/java-storage/issues/1160)) ([5a160ee](https://github.com/googleapis/java-storage/commit/5a160eee2b80e3d392df9d73dfc30ca9cd665764))


### Dependencies

* update dependency org.easymock:easymock to v4 ([#1198](https://github.com/googleapis/java-storage/issues/1198)) ([558520f](https://github.com/googleapis/java-storage/commit/558520f35ed64f0b36f7f8ada4491023a0fb759e))
* update kms.version to v0.94.1 ([#1195](https://github.com/googleapis/java-storage/issues/1195)) ([cc999b1](https://github.com/googleapis/java-storage/commit/cc999b1ebaba051524ce6131052c824232ccb79a))

### [2.2.3](https://www.github.com/googleapis/java-storage/compare/v2.2.2...v2.2.3) (2022-01-07)


### Bug Fixes

* do not cause a failure when encountering no bindings ([#1177](https://www.github.com/googleapis/java-storage/issues/1177)) ([16c2aef](https://www.github.com/googleapis/java-storage/commit/16c2aef4f09eccee59d1028e3bbf01c65b5982d6))
* **java:** add -ntp flag to native image testing command ([#1169](https://www.github.com/googleapis/java-storage/issues/1169)) ([b8a6395](https://www.github.com/googleapis/java-storage/commit/b8a6395fcaa34423d42a90bd42f71809f89a6c3b))
* update retry handling to retry idempotent requests that encounter unexpected EOF while parsing json responses ([#1155](https://www.github.com/googleapis/java-storage/issues/1155)) ([8fbe6ef](https://www.github.com/googleapis/java-storage/commit/8fbe6efab969d699e9ba9e5448db7a6ee10c0572))


### Documentation

* add new sample storage_configure_retries ([#1152](https://www.github.com/googleapis/java-storage/issues/1152)) ([8634c4b](https://www.github.com/googleapis/java-storage/commit/8634c4b5cb88d2818378558427170ecf6c403df5))
* update comments ([#1188](https://www.github.com/googleapis/java-storage/issues/1188)) ([d58e67c](https://www.github.com/googleapis/java-storage/commit/d58e67c217f38ca7b1926882ec48bd7b0c351ea7))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.6.0 ([#1191](https://www.github.com/googleapis/java-storage/issues/1191)) ([3b384cf](https://www.github.com/googleapis/java-storage/commit/3b384cf46876610ce33f2842ee8e9fc13e08443c))
* update dependency org.apache.httpcomponents:httpcore to v4.4.15 ([#1171](https://www.github.com/googleapis/java-storage/issues/1171)) ([57f7a74](https://www.github.com/googleapis/java-storage/commit/57f7a743ee042c52261cd388fb0aec48c84e5d32))

### [2.2.2](https://www.github.com/googleapis/java-storage/compare/v2.2.1...v2.2.2) (2021-12-06)


### Bug Fixes

* update StorageOptions to not overwrite any previously set host ([#1142](https://www.github.com/googleapis/java-storage/issues/1142)) ([05375c0](https://www.github.com/googleapis/java-storage/commit/05375c0b9b6f9fde2e6cefb1af6a695aa3b01732))


### Documentation

* Add comments to GCS gRPC API proto spec to describe how naming work ([#1139](https://www.github.com/googleapis/java-storage/issues/1139)) ([417c525](https://www.github.com/googleapis/java-storage/commit/417c5250eb7ad1a7b04a055a39d72e6536a63e18))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20211201-1.32.1 ([#1165](https://www.github.com/googleapis/java-storage/issues/1165)) ([9031836](https://www.github.com/googleapis/java-storage/commit/90318368e69d7677c49e985eb58ff1b61d878ec9))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.5.1 ([#1163](https://www.github.com/googleapis/java-storage/issues/1163)) ([feca2c6](https://www.github.com/googleapis/java-storage/commit/feca2c6342786ef3fb699c459067c015bd374a13))
* update kms.version to v0.94.0 ([#1164](https://www.github.com/googleapis/java-storage/issues/1164)) ([8653783](https://www.github.com/googleapis/java-storage/commit/86537836a3b96f369e1cad59c692d350047414f7))

### [2.2.1](https://www.github.com/googleapis/java-storage/compare/v2.2.0...v2.2.1) (2021-11-15)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.5.0 ([#1146](https://www.github.com/googleapis/java-storage/issues/1146)) ([a5d13a9](https://www.github.com/googleapis/java-storage/commit/a5d13a97bae50b4ee8a2fcef180ddc26b77e3d16))

## [2.2.0](https://www.github.com/googleapis/java-storage/compare/v2.1.9...v2.2.0) (2021-11-02)


### Features

* next release from mainline is 2.2.0 ([#1124](https://www.github.com/googleapis/java-storage/issues/1124)) ([53a755b](https://www.github.com/googleapis/java-storage/commit/53a755b315c0e739e33929fa5db92eb1daf32e8b))
* update all automatic retry behavior to be idempotency aware ([#1132](https://www.github.com/googleapis/java-storage/issues/1132)) ([470b8cd](https://www.github.com/googleapis/java-storage/commit/470b8cd8a24c1c2b4be1b956d1691dbae8cf87fd))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20211018-1.32.1 ([#1123](https://www.github.com/googleapis/java-storage/issues/1123)) ([edc0e00](https://www.github.com/googleapis/java-storage/commit/edc0e00a9f0d3c48ed7abbd5b01429837298ecfb))
* update kms.version to v0.93.2 ([#1120](https://www.github.com/googleapis/java-storage/issues/1120)) ([a5c007d](https://www.github.com/googleapis/java-storage/commit/a5c007d306c5d7fc00927be39b6879dfc7a01fcb))

### [2.1.9](https://www.github.com/googleapis/java-storage/compare/v2.1.8...v2.1.9) (2021-10-19)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.4.0 ([#1115](https://www.github.com/googleapis/java-storage/issues/1115)) ([37d892b](https://www.github.com/googleapis/java-storage/commit/37d892b05ae3c7338b6c804cddfcecca80509ea3))

### [2.1.8](https://www.github.com/googleapis/java-storage/compare/v2.1.7...v2.1.8) (2021-10-18)


### Bug Fixes

* regenerate google.cloud.storage.v2 protos ([a7e3b94](https://www.github.com/googleapis/java-storage/commit/a7e3b94e4a3e03599b0dbe51fbe574ed4ea1a0d8))


### Dependencies

* update kms.version to v0.93.1 ([#1079](https://www.github.com/googleapis/java-storage/issues/1079)) ([1c52b3d](https://www.github.com/googleapis/java-storage/commit/1c52b3db6699c2ad325853e95231e1a908da069f))

### [2.1.7](https://www.github.com/googleapis/java-storage/compare/v2.1.6...v2.1.7) (2021-10-04)


### Bug Fixes

* update PAP to use inherited instead of unspecified ([#1051](https://www.github.com/googleapis/java-storage/issues/1051)) ([6d73e46](https://www.github.com/googleapis/java-storage/commit/6d73e4631777542996a0ea815b482f5c19a8927d))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20210918-1.32.1 ([#1046](https://www.github.com/googleapis/java-storage/issues/1046)) ([2c79005](https://www.github.com/googleapis/java-storage/commit/2c79005d29ee0b279850c7008b1afbb302f9c90d))
* update kms.version to v0.93.0 ([#1061](https://www.github.com/googleapis/java-storage/issues/1061)) ([97b1a2e](https://www.github.com/googleapis/java-storage/commit/97b1a2ebe411e48e2df095fe5518a867c5136851))

### [2.1.6](https://www.github.com/googleapis/java-storage/compare/v2.1.5...v2.1.6) (2021-09-24)


### Dependencies

* update kms.version to v0.92.2 ([#1039](https://www.github.com/googleapis/java-storage/issues/1039)) ([d6a0542](https://www.github.com/googleapis/java-storage/commit/d6a0542f5fd290a0bdc2755f81a49f55724662b2))

### [2.1.5](https://www.github.com/googleapis/java-storage/compare/v2.1.4...v2.1.5) (2021-09-22)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.3.0 ([#1035](https://www.github.com/googleapis/java-storage/issues/1035)) ([ae71c24](https://www.github.com/googleapis/java-storage/commit/ae71c2496f64a0601b24574032cc133afb423408))

### [2.1.4](https://www.github.com/googleapis/java-storage/compare/v2.1.3...v2.1.4) (2021-09-20)


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20210914-1.32.1 ([#1025](https://www.github.com/googleapis/java-storage/issues/1025)) ([ff56d5e](https://www.github.com/googleapis/java-storage/commit/ff56d5e5632d925542ac918d293b68dfcb32b465))
* update kms.version to v0.92.1 ([#1023](https://www.github.com/googleapis/java-storage/issues/1023)) ([ca1afcf](https://www.github.com/googleapis/java-storage/commit/ca1afcff085bd02b150b93128b102cb9a61e1b4d))

### [2.1.3](https://www.github.com/googleapis/java-storage/compare/v2.1.2...v2.1.3) (2021-09-15)


### Dependencies

* update kms.version to v0.92.0 ([#1018](https://www.github.com/googleapis/java-storage/issues/1018)) ([f1c58db](https://www.github.com/googleapis/java-storage/commit/f1c58db517596a5ee65e0f8a6e4b9c561288594e))

### [2.1.2](https://www.github.com/googleapis/java-storage/compare/v2.1.1...v2.1.2) (2021-09-14)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.2.1 ([#1011](https://www.github.com/googleapis/java-storage/issues/1011)) ([0bf06a5](https://www.github.com/googleapis/java-storage/commit/0bf06a54e3b90b9d8cf425d490561b48d6b5d882))

### [2.1.1](https://www.github.com/googleapis/java-storage/compare/v2.1.0...v2.1.1) (2021-09-03)


### Documentation

* Modify OLM notice to recommend upgrading to latest version. ([#932](https://www.github.com/googleapis/java-storage/issues/932)) ([be72433](https://www.github.com/googleapis/java-storage/commit/be72433ef5446db880e44f103a7d120f444f183f))


### Dependencies

* update dependency com.google.cloud:google-cloud-conformance-tests to v0.2.0 ([#982](https://www.github.com/googleapis/java-storage/issues/982)) ([c7460a3](https://www.github.com/googleapis/java-storage/commit/c7460a3ffef81ef2f651b582a97139c0523d1eab))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.2.0 ([#989](https://www.github.com/googleapis/java-storage/issues/989)) ([6745c9e](https://www.github.com/googleapis/java-storage/commit/6745c9e5a9d3a907873b989ca8f8a47edd833523))
* update kms.version to v0.91.3 ([#991](https://www.github.com/googleapis/java-storage/issues/991)) ([1f15022](https://www.github.com/googleapis/java-storage/commit/1f15022a590bce4f80dcb86d150b8e3dbe43aec9))

## [2.1.0](https://www.github.com/googleapis/java-storage/compare/v2.0.2...v2.1.0) (2021-08-24)


### Features

* fix post policy escape bug, update conformance tests ([#924](https://www.github.com/googleapis/java-storage/issues/924)) ([d8329c3](https://www.github.com/googleapis/java-storage/commit/d8329c34fe19fd8c6bba5579aa3c55490c1d4e6f))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.1.0 ([#976](https://www.github.com/googleapis/java-storage/issues/976)) ([5cac14d](https://www.github.com/googleapis/java-storage/commit/5cac14d7785ef3798c379d17cd44500958d9cc6a))
* update kms.version to v0.91.2 ([#977](https://www.github.com/googleapis/java-storage/issues/977)) ([1c60e6e](https://www.github.com/googleapis/java-storage/commit/1c60e6e6a34f662478043989b5b0bddea32cc5bf))

### [2.0.2](https://www.github.com/googleapis/java-storage/compare/v2.0.1...v2.0.2) (2021-08-19)


### Dependencies

* update kms.version to v0.91.1 ([#956](https://www.github.com/googleapis/java-storage/issues/956)) ([53d24e9](https://www.github.com/googleapis/java-storage/commit/53d24e9d3e27c0319fa3b6837c926484b1bd56a4))

## [2.0.1](https://www.github.com/googleapis/java-storage/compare/v2.0.0...v2.0.1) (2021-08-11)


### Features

* generate storage v2 gapic client ([#960](https://www.github.com/googleapis/java-storage/issues/960)) ([fb2f9d4](https://www.github.com/googleapis/java-storage/commit/fb2f9d489e42b57f61642ce9e0c1a65fe91c9c45))


### Bug Fixes

* incorrectly labeled span list(String,Map) ([#946](https://www.github.com/googleapis/java-storage/issues/946)) ([0c1fdcf](https://www.github.com/googleapis/java-storage/commit/0c1fdcfe89609b10c148f0dc6026084d2f49b1b7))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2.0.1 ([#961](https://www.github.com/googleapis/java-storage/issues/961)) ([69543dc](https://www.github.com/googleapis/java-storage/commit/69543dcba2fce1028e5fac25a59e1defe6465f06))

## [2.0.0](https://www.github.com/googleapis/java-storage/compare/v1.118.1...v2.0.0) (2021-08-09)


### ⚠ BREAKING CHANGES

* migrate to java8 (#950)

### Features

* migrate to java8 ([#950](https://www.github.com/googleapis/java-storage/issues/950)) ([839bcc1](https://www.github.com/googleapis/java-storage/commit/839bcc174ff1c2f5536130d880a5c6e2559b5793))

### [1.118.1](https://www.github.com/googleapis/java-storage/compare/v1.118.0...v1.118.1) (2021-08-06)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v2 ([#941](https://www.github.com/googleapis/java-storage/issues/941)) ([effefa6](https://www.github.com/googleapis/java-storage/commit/effefa64336a6112dae1497b3bcde7c7f8b0ad41))

## [1.118.0](https://www.github.com/googleapis/java-storage/compare/v1.117.1...v1.118.0) (2021-07-13)


### Features

* fix signed url mismatch in BlobWriteChannel ([#915](https://www.github.com/googleapis/java-storage/issues/915)) ([8b05867](https://www.github.com/googleapis/java-storage/commit/8b0586757523cfc550c62ff264eea3eebbd7f32e))


### Bug Fixes

* correct lastChunk retry logic in BlobWriteChannel ([#918](https://www.github.com/googleapis/java-storage/issues/918)) ([ab0228c](https://www.github.com/googleapis/java-storage/commit/ab0228c95df831d79f4a9c993908e5700dab5aa7))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20210127-1.32.1 ([#910](https://www.github.com/googleapis/java-storage/issues/910)) ([2c54acc](https://www.github.com/googleapis/java-storage/commit/2c54acca0653a96773ab3606a8d97299e9fdf045))
* update kms.version to v0.90.0 ([#911](https://www.github.com/googleapis/java-storage/issues/911)) ([1050725](https://www.github.com/googleapis/java-storage/commit/1050725c91b4375340ba113568ba04538c7f52fc))

### [1.117.1](https://www.github.com/googleapis/java-storage/compare/v1.117.0...v1.117.1) (2021-06-30)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v1.4.0 ([#905](https://www.github.com/googleapis/java-storage/issues/905)) ([dd084d1](https://www.github.com/googleapis/java-storage/commit/dd084d16b5f0bbf54730f2e91ce2c04a61457e0f))

## [1.117.0](https://www.github.com/googleapis/java-storage/compare/v1.116.0...v1.117.0) (2021-06-28)


### Features

* Add from and to storage url options for BlobId ([#888](https://www.github.com/googleapis/java-storage/issues/888)) ([1876a58](https://www.github.com/googleapis/java-storage/commit/1876a580f904d095ca6621c1e2f38c3a6e253276))
* add support of public access prevention ([#636](https://www.github.com/googleapis/java-storage/issues/636)) ([3d1e482](https://www.github.com/googleapis/java-storage/commit/3d1e48208c44c35c8e3761913bcd05c438e81069))


### Bug Fixes

* Add `shopt -s nullglob` to dependencies script ([#894](https://www.github.com/googleapis/java-storage/issues/894)) ([901fd33](https://www.github.com/googleapis/java-storage/commit/901fd335c8d2f2e49844dee2adfa318a98ed99ba))
* Update dependencies.sh to not break on mac ([#879](https://www.github.com/googleapis/java-storage/issues/879)) ([bc6d1d9](https://www.github.com/googleapis/java-storage/commit/bc6d1d9e211fbbb1accd1019c8eed4bc55ca421c))


### Documentation

* add notice about broken OLM experience ([#898](https://www.github.com/googleapis/java-storage/issues/898)) ([73e7cdf](https://www.github.com/googleapis/java-storage/commit/73e7cdf162be76a8438160f4c7f2070fb6fb5ea6))


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20210127-1.31.5 ([#889](https://www.github.com/googleapis/java-storage/issues/889)) ([99138a4](https://www.github.com/googleapis/java-storage/commit/99138a4cd3523cc634e3c5283a775a1c245b6201))

## [1.116.0](https://www.github.com/googleapis/java-storage/compare/v1.115.0...v1.116.0) (2021-06-14)


### Features

* Add shouldReturnRawInputStream option to Get requests ([#872](https://www.github.com/googleapis/java-storage/issues/872)) ([474dfae](https://www.github.com/googleapis/java-storage/commit/474dfaec09d591455cecc77b08461efff1010c3a))


### Bug Fixes

* **ci:** remove linkage-monitor to pass 1.106.1 patch ci ([#862](https://www.github.com/googleapis/java-storage/issues/862)) ([94a9159](https://www.github.com/googleapis/java-storage/commit/94a915958f888cfbf4110d06a7f64be135dc141e))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v1.3.0 ([#863](https://www.github.com/googleapis/java-storage/issues/863)) ([37bfd5e](https://www.github.com/googleapis/java-storage/commit/37bfd5e3cf1c62767ff8033366cca66c2e8e6e4c))
* update kms.version ([#860](https://www.github.com/googleapis/java-storage/issues/860)) ([f1430ff](https://www.github.com/googleapis/java-storage/commit/f1430ffea07696ea808369fcd287187c14afc9a2))
* update kms.version to v0.89.3 ([#873](https://www.github.com/googleapis/java-storage/issues/873)) ([ee7c236](https://www.github.com/googleapis/java-storage/commit/ee7c2368928c050befb809a2d61bd6ffc92bdc88))

## [1.115.0](https://www.github.com/googleapis/java-storage/compare/v1.114.0...v1.115.0) (2021-06-01)


### Features

* add `gcf-owl-bot[bot]` to `ignoreAuthors` ([#837](https://www.github.com/googleapis/java-storage/issues/837)) ([fe8e98a](https://www.github.com/googleapis/java-storage/commit/fe8e98a229f472c1f29d206d937690660bfa1444))


### Bug Fixes

* improve error detection and reporting for BlobWriteChannel retry state ([#846](https://www.github.com/googleapis/java-storage/issues/846)) ([d0f2184](https://www.github.com/googleapis/java-storage/commit/d0f2184f4dd2d99a4315f260f35421358d14a2df)), closes [#839](https://www.github.com/googleapis/java-storage/issues/839)
* update BucketInfo translation code to properly handle lifecycle rules ([#852](https://www.github.com/googleapis/java-storage/issues/852)) ([3b1df1d](https://www.github.com/googleapis/java-storage/commit/3b1df1d00a459b134103bc8738f0294188502a37)), closes [#850](https://www.github.com/googleapis/java-storage/issues/850)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v1.2.0 ([#836](https://www.github.com/googleapis/java-storage/issues/836)) ([c1752ce](https://www.github.com/googleapis/java-storage/commit/c1752ce17d5d723d0ea36c41d98ae2bc9201fec2))
* update kms.version to v0.88.4 ([#830](https://www.github.com/googleapis/java-storage/issues/830)) ([7e3dc28](https://www.github.com/googleapis/java-storage/commit/7e3dc287e4285a9312393179671a78c569e7e869))
* update kms.version to v0.89.0 ([#855](https://www.github.com/googleapis/java-storage/issues/855)) ([29236e9](https://www.github.com/googleapis/java-storage/commit/29236e9d2eefb0e64b1b9bbfc532f4c3ae3e9ea4))

## [1.114.0](https://www.github.com/googleapis/java-storage/compare/v1.113.16...v1.114.0) (2021-05-13)


### Features

* Remove client side vaildation for lifecycle conditions ([#816](https://www.github.com/googleapis/java-storage/issues/816)) ([5ec84cc](https://www.github.com/googleapis/java-storage/commit/5ec84cc2935a4787dd14a207d27501878f5849d5))


### Bug Fixes

* **test:** update blob paths used in storage.it.ITStorageTest#testDownloadPublicBlobWithoutAuthentication ([#759](https://www.github.com/googleapis/java-storage/issues/759)) ([#817](https://www.github.com/googleapis/java-storage/issues/817)) ([1a576ca](https://www.github.com/googleapis/java-storage/commit/1a576ca3945b51d7a678aa2414be91b3c6b2d55e))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.31.5 ([#820](https://www.github.com/googleapis/java-storage/issues/820)) ([9e1bc0b](https://www.github.com/googleapis/java-storage/commit/9e1bc0b42abdaab0b11d761ecdbb92f6116aacd2))
* update dependency com.google.api.grpc:grpc-google-cloud-kms-v1 to v0.88.3 ([#797](https://www.github.com/googleapis/java-storage/issues/797)) ([747e7e4](https://www.github.com/googleapis/java-storage/commit/747e7e463c028b9cf8a406b7536b1916c1d52c01))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v1.1.0 ([#815](https://www.github.com/googleapis/java-storage/issues/815)) ([e210de9](https://www.github.com/googleapis/java-storage/commit/e210de93452243242be7d3d719d00da723632335))

### [1.113.16](https://www.github.com/googleapis/java-storage/compare/v1.113.15...v1.113.16) (2021-04-23)


### Bug Fixes

* release scripts from issuing overlapping phases ([#784](https://www.github.com/googleapis/java-storage/issues/784)) ([36751f5](https://www.github.com/googleapis/java-storage/commit/36751f5de9708ac9e23550f67256fb05ebf1f69e))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.21.1 ([#789](https://www.github.com/googleapis/java-storage/issues/789)) ([c005e87](https://www.github.com/googleapis/java-storage/commit/c005e877a7d64c4bbd2ed267526d8025ea29a9ad))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v1 ([#794](https://www.github.com/googleapis/java-storage/issues/794)) ([195fead](https://www.github.com/googleapis/java-storage/commit/195fead94dea4c50f7e285e0a7a1578fa5b6265d))

### [1.113.15](https://www.github.com/googleapis/java-storage/compare/v1.113.14...v1.113.15) (2021-04-13)


### Bug Fixes

* **test:** update blob paths used in storage.it.ITStorageTest#testDownloadPublicBlobWithoutAuthentication ([#759](https://www.github.com/googleapis/java-storage/issues/759)) ([9a6619c](https://www.github.com/googleapis/java-storage/commit/9a6619c39a89e2c2ee8d0000d595d09ac7b7825f))
* typo ([#779](https://www.github.com/googleapis/java-storage/issues/779)) ([3c3d6b4](https://www.github.com/googleapis/java-storage/commit/3c3d6b487648fde4eb956ce8912cd680a4440f8d))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.31.4 ([#774](https://www.github.com/googleapis/java-storage/issues/774)) ([ad9ff7b](https://www.github.com/googleapis/java-storage/commit/ad9ff7b801d0c5fb39f72c7118c319f4e45084a0))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.21.0 ([#771](https://www.github.com/googleapis/java-storage/issues/771)) ([5605095](https://www.github.com/googleapis/java-storage/commit/5605095ed796327879a930c12526b3c5b1409b17))
* update kms.version to v0.88.1 ([#758](https://www.github.com/googleapis/java-storage/issues/758)) ([3e57ea9](https://www.github.com/googleapis/java-storage/commit/3e57ea9a2f5f7013e997469c5ca32be8cef2a4a4))
* update kms.version to v0.88.2 ([#778](https://www.github.com/googleapis/java-storage/issues/778)) ([6edfc4c](https://www.github.com/googleapis/java-storage/commit/6edfc4ced2bdae9878ecdbc5ef636ac39bdb5881))
* update truth ([#767](https://www.github.com/googleapis/java-storage/issues/767)) ([4e5ee03](https://www.github.com/googleapis/java-storage/commit/4e5ee0398e700baf4f88224f66309e426f9532d7))

### [1.113.14](https://www.github.com/googleapis/java-storage/compare/v1.113.13...v1.113.14) (2021-03-11)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.20.1 ([#749](https://www.github.com/googleapis/java-storage/issues/749)) ([bb42107](https://www.github.com/googleapis/java-storage/commit/bb42107ff10148e14e112ff78534753f2ebc7dd9))
* update kms.version to v0.88.0 ([#753](https://www.github.com/googleapis/java-storage/issues/753)) ([eaedb64](https://www.github.com/googleapis/java-storage/commit/eaedb6456f2f427a7f2f3f3d6bd13d0d49fd269b))

### [1.113.13](https://www.github.com/googleapis/java-storage/compare/v1.113.12...v1.113.13) (2021-03-08)


### Bug Fixes

* npe in createFrom ([#746](https://www.github.com/googleapis/java-storage/issues/746)) ([9ed9d13](https://www.github.com/googleapis/java-storage/commit/9ed9d1389e92766b66e2b8b4fb78b44d96d98803))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.31.3 ([#737](https://www.github.com/googleapis/java-storage/issues/737)) ([71b3842](https://www.github.com/googleapis/java-storage/commit/71b384233226531eabc1bd8eebf716ec53708afc))

### [1.113.12](https://www.github.com/googleapis/java-storage/compare/v1.113.11...v1.113.12) (2021-02-26)


### Bug Fixes

* retrying get remote offset and recover from last chunk failures. ([#726](https://www.github.com/googleapis/java-storage/issues/726)) ([b41b881](https://www.github.com/googleapis/java-storage/commit/b41b88109e13b5ebbd0393d1f264225c12876be6))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.31.2 ([#686](https://www.github.com/googleapis/java-storage/issues/686)) ([6b1f036](https://www.github.com/googleapis/java-storage/commit/6b1f0361376167719ec5456181134136d27d1d3c))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.20.0 ([#732](https://www.github.com/googleapis/java-storage/issues/732)) ([c98413d](https://www.github.com/googleapis/java-storage/commit/c98413df9d9514340aed78b5a4d5e596760bb616))
* update kms.version to v0.87.7 ([#724](https://www.github.com/googleapis/java-storage/issues/724)) ([3229bd8](https://www.github.com/googleapis/java-storage/commit/3229bd860f3a4d700a969aa9e922bbf6b5c1ca10))
* update kms.version to v0.87.8 ([#733](https://www.github.com/googleapis/java-storage/issues/733)) ([a21b75f](https://www.github.com/googleapis/java-storage/commit/a21b75fa846f373970298dd98f8f3520fc2b3c97))

### [1.113.11](https://www.github.com/googleapis/java-storage/compare/v1.113.10...v1.113.11) (2021-02-19)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.19.0 ([#719](https://www.github.com/googleapis/java-storage/issues/719)) ([5831bfa](https://www.github.com/googleapis/java-storage/commit/5831bfae3afeab9b044c8d53ebf6a2ce79bc9950))

### [1.113.10](https://www.github.com/googleapis/java-storage/compare/v1.113.9...v1.113.10) (2021-02-17)


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20210127-1.31.0 ([#706](https://www.github.com/googleapis/java-storage/issues/706)) ([04db8f7](https://www.github.com/googleapis/java-storage/commit/04db8f7b87644559685d4c05a67a74e4c8bea364))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.18.0 ([#683](https://www.github.com/googleapis/java-storage/issues/683)) ([6f172eb](https://www.github.com/googleapis/java-storage/commit/6f172eba6fd6e9c11a1f49569249ea6e714ea91f))
* update kms.version to v0.87.6 ([#702](https://www.github.com/googleapis/java-storage/issues/702)) ([a50c333](https://www.github.com/googleapis/java-storage/commit/a50c333f6e944fa4c6bdf9613cddca7c4fe79652))

### [1.113.9](https://www.github.com/googleapis/java-storage/compare/v1.113.8...v1.113.9) (2021-01-12)


### Bug Fixes

* last chunk is retriable ([#677](https://www.github.com/googleapis/java-storage/issues/677)) ([44f49e0](https://www.github.com/googleapis/java-storage/commit/44f49e0a33c3e541d9f8b22622ffff17cc8b8eaa))
* unnecessary options in resumable upload URL ([#679](https://www.github.com/googleapis/java-storage/issues/679)) ([d31a39b](https://www.github.com/googleapis/java-storage/commit/d31a39b88b2d8adb04549330f9b8ff1c1a516b69))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.17.1 ([#678](https://www.github.com/googleapis/java-storage/issues/678)) ([d4a237f](https://www.github.com/googleapis/java-storage/commit/d4a237f4dff9dd870a69d5da9d690c14d4e88610))
* update kms.version to v0.87.5 ([#662](https://www.github.com/googleapis/java-storage/issues/662)) ([20e7c1f](https://www.github.com/googleapis/java-storage/commit/20e7c1f10a233df6d4660b31d26cd95a6d4002e9))

### [1.113.8](https://www.github.com/googleapis/java-storage/compare/v1.113.7...v1.113.8) (2020-12-16)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.17.0 ([#659](https://www.github.com/googleapis/java-storage/issues/659)) ([5fa03fa](https://www.github.com/googleapis/java-storage/commit/5fa03fa14aa9ee29e7b1b27b783ab873052b97c6))

### [1.113.7](https://www.github.com/googleapis/java-storage/compare/v1.113.6...v1.113.7) (2020-12-14)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.16.1 ([f1fc884](https://www.github.com/googleapis/java-storage/commit/f1fc884851ee602d737f3e4191acb1f8450c8f2c))

### [1.113.6](https://www.github.com/googleapis/java-storage/compare/v1.113.5...v1.113.6) (2020-12-10)


### Bug Fixes

* content-length missing in offset request ([#647](https://www.github.com/googleapis/java-storage/issues/647)) ([3cd3815](https://www.github.com/googleapis/java-storage/commit/3cd3815c62603d05d4c571ba1affeaf91e4d8040))


### Dependencies

* update kms.version to v0.87.3 ([#646](https://www.github.com/googleapis/java-storage/issues/646)) ([c93896a](https://www.github.com/googleapis/java-storage/commit/c93896a5007b48753809de806ddaf6c8df6e9d56))

### [1.113.5](https://www.github.com/googleapis/java-storage/compare/v1.113.4...v1.113.5) (2020-12-07)


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.31.1 ([#611](https://www.github.com/googleapis/java-storage/issues/611)) ([7c4c759](https://www.github.com/googleapis/java-storage/commit/7c4c759d8bca9c20252e06e02eb8ead3bd9f88d6))
* update dependency com.google.api.grpc:grpc-google-cloud-kms-v1 to v0.87.2 ([#625](https://www.github.com/googleapis/java-storage/issues/625)) ([243a3cb](https://www.github.com/googleapis/java-storage/commit/243a3cb1506b2e2d609210dc4e9608637c06d7f3))
* update dependency com.google.apis:google-api-services-storage to v1-rev20201112-1.30.10 ([#613](https://www.github.com/googleapis/java-storage/issues/613)) ([b0e24db](https://www.github.com/googleapis/java-storage/commit/b0e24db88c784fd05988a813bd8b29aeff0739f2))
* update dependency com.google.apis:google-api-services-storage to v1-rev20201112-1.31.0 ([#641](https://www.github.com/googleapis/java-storage/issues/641)) ([11da9c7](https://www.github.com/googleapis/java-storage/commit/11da9c7e9058c508423e7b2f84c897ab3e9ab3f3))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.16.0 ([#639](https://www.github.com/googleapis/java-storage/issues/639)) ([68a3af9](https://www.github.com/googleapis/java-storage/commit/68a3af9b12c0e05d2cf59fb58aceab15323e29b1))
* update dependency org.apache.httpcomponents:httpcore to v4.4.14 ([#637](https://www.github.com/googleapis/java-storage/issues/637)) ([af53902](https://www.github.com/googleapis/java-storage/commit/af5390239ffd1e157f066a1009b7bb18fa6264ec))

### [1.113.4](https://www.github.com/googleapis/java-storage/compare/v1.113.3...v1.113.4) (2020-11-13)


### Bug Fixes

* retry using remote offset ([#604](https://www.github.com/googleapis/java-storage/issues/604)) ([216b52c](https://www.github.com/googleapis/java-storage/commit/216b52c54d34eaf1307788809a3512c461adf381))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.15.0 ([#610](https://www.github.com/googleapis/java-storage/issues/610)) ([ac65e5b](https://www.github.com/googleapis/java-storage/commit/ac65e5b0bd324d5726504bb3405c758675a56ddc))

### [1.113.3](https://www.github.com/googleapis/java-storage/compare/v1.113.2...v1.113.3) (2020-11-06)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.14.1 ([#592](https://www.github.com/googleapis/java-storage/issues/592)) ([25e8e6a](https://www.github.com/googleapis/java-storage/commit/25e8e6a01dde517fd42cfc8ae59b8555ea0a2831))
* update kms.version to v0.87.1 ([#595](https://www.github.com/googleapis/java-storage/issues/595)) ([1e399cd](https://www.github.com/googleapis/java-storage/commit/1e399cd33755e647bf08f4a82af932320cab655d))

### [1.113.2](https://www.github.com/googleapis/java-storage/compare/v1.113.1...v1.113.2) (2020-10-26)


### Documentation

* update libraries-bom ([#540](https://www.github.com/googleapis/java-storage/issues/540)) ([54987e1](https://www.github.com/googleapis/java-storage/commit/54987e1ba35d99db680ab2ad6ac86a6b74c7c705))
* update libraries-bom ([#552](https://www.github.com/googleapis/java-storage/issues/552)) ([c4df018](https://www.github.com/googleapis/java-storage/commit/c4df01875b8f088bd65bcd0353e1b74a18b9582c))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.30.11 ([#575](https://www.github.com/googleapis/java-storage/issues/575)) ([99838e6](https://www.github.com/googleapis/java-storage/commit/99838e63f9a71095c4d8f6c99622a9aee2e5d26d))
* update dependency com.google.apis:google-api-services-storage to v1-rev20200927-1.30.10 ([#539](https://www.github.com/googleapis/java-storage/issues/539)) ([5e49013](https://www.github.com/googleapis/java-storage/commit/5e49013add340e4d8287e00b8d4a9c499df80205))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.10.0 ([#529](https://www.github.com/googleapis/java-storage/issues/529)) ([dc58856](https://www.github.com/googleapis/java-storage/commit/dc58856c2548013a495b62cc6bb696ada24d2557))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.10.2 ([#549](https://www.github.com/googleapis/java-storage/issues/549)) ([c59c28d](https://www.github.com/googleapis/java-storage/commit/c59c28d97a9eb4e811921c7cad637d67c2be16be))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.12.1 ([#566](https://www.github.com/googleapis/java-storage/issues/566)) ([f1dedfb](https://www.github.com/googleapis/java-storage/commit/f1dedfbf9f47c87c7f7fea5e6c1c7c1af35b060e))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.13.0 ([#570](https://www.github.com/googleapis/java-storage/issues/570)) ([ef55f49](https://www.github.com/googleapis/java-storage/commit/ef55f49230f58863195782b5fe0f84285a316aef))

### [1.113.1](https://www.github.com/googleapis/java-storage/compare/v1.113.0...v1.113.1) (2020-09-17)


### Bug Fixes

* KMS Bad Key error when using existing Blob context to overwrite object ([#507](https://www.github.com/googleapis/java-storage/issues/507)) ([4d9c490](https://www.github.com/googleapis/java-storage/commit/4d9c49027e4746ee273902694441886c2f43188d))
* When passing a sub-array (offset, length) to the Storage#create method the array is needlessly cloned  ([#506](https://www.github.com/googleapis/java-storage/issues/506)) ([9415bb7](https://www.github.com/googleapis/java-storage/commit/9415bb7bdb42d8012ca457a90070b616e6bbec19)), closes [#505](https://www.github.com/googleapis/java-storage/issues/505)


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20200814-1.30.10 ([#499](https://www.github.com/googleapis/java-storage/issues/499)) ([af91d7d](https://www.github.com/googleapis/java-storage/commit/af91d7da4117fb22992d6a860af61f72906e0aa1))


### Documentation

* update libraries-bom ([#504](https://www.github.com/googleapis/java-storage/issues/504)) ([0e58c1c](https://www.github.com/googleapis/java-storage/commit/0e58c1cb2b6a890e567b043188613021592f2bc8))

## [1.113.0](https://www.github.com/googleapis/java-storage/compare/v1.112.0...v1.113.0) (2020-09-03)


### Features

* expose timeStorageClassUpdated property of blob's ([#456](https://www.github.com/googleapis/java-storage/issues/456)) ([57853ec](https://www.github.com/googleapis/java-storage/commit/57853ec7fbc2f3188d8da991001660a4f6008632))


### Bug Fixes

* add missing FieldSelector inside BucketField and BlobField ([#484](https://www.github.com/googleapis/java-storage/issues/484)) ([c2aa9cf](https://www.github.com/googleapis/java-storage/commit/c2aa9cf6fb4c7f407cbfce85b338b735ceafe1dc))
* prevent NPE in RemoteStorageHelper.cleanBuckets ([#492](https://www.github.com/googleapis/java-storage/issues/492)) ([db358c8](https://www.github.com/googleapis/java-storage/commit/db358c8b53f7ba3084c5566c9abf4033bf29783f))
* set IT_SERVICE_ACCOUNT_EMAIL for nightly integration test ([#479](https://www.github.com/googleapis/java-storage/issues/479)) ([23c379e](https://www.github.com/googleapis/java-storage/commit/23c379e4d28e4fb319db047c7d46654d9a8b9a61))


### Documentation

* update libraries-bom ([#494](https://www.github.com/googleapis/java-storage/issues/494)) ([6b015da](https://www.github.com/googleapis/java-storage/commit/6b015da57d42f468c9b3d1f86476407a61cd14ea))
* update link ([#490](https://www.github.com/googleapis/java-storage/issues/490)) ([6cd5dfa](https://www.github.com/googleapis/java-storage/commit/6cd5dface9cc14f2ec6729e5b842bcee91c1ad34))


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.9.0 ([#493](https://www.github.com/googleapis/java-storage/issues/493)) ([0e4f70f](https://www.github.com/googleapis/java-storage/commit/0e4f70f7f70784fee91be499def9734d8af61be9))
* update kms.version to v0.87.0 ([#489](https://www.github.com/googleapis/java-storage/issues/489)) ([a045d54](https://www.github.com/googleapis/java-storage/commit/a045d5400234595f102a8b8d594539dbfd5f295e))

## [1.112.0](https://www.github.com/googleapis/java-storage/compare/v1.111.2...v1.112.0) (2020-08-27)


### Features

* add support of customTime metadata ([#413](https://www.github.com/googleapis/java-storage/issues/413)) ([6f4585e](https://www.github.com/googleapis/java-storage/commit/6f4585eb6706390865cf5fb565fa8062d0071045))
* add support of customTimeBefore and daysSinceCustomTime ([#396](https://www.github.com/googleapis/java-storage/issues/396)) ([1af8288](https://www.github.com/googleapis/java-storage/commit/1af8288016f2526ddbe221ef22dc705e28b18b77))
* add support of daysSinceNoncurrentTime and noncurrentTimeBefore OLM options ([#335](https://www.github.com/googleapis/java-storage/issues/335)) ([1e3e88a](https://www.github.com/googleapis/java-storage/commit/1e3e88a391651421469e5c7a8216a788eaa4ba5a))
* add support of null to remove the CORS configuration from bucket  ([#438](https://www.github.com/googleapis/java-storage/issues/438)) ([f8a4b12](https://www.github.com/googleapis/java-storage/commit/f8a4b12517c661881d7b7c65f796c1c8f1cf3ae9))
* add support of startOffset and endOffset ([#430](https://www.github.com/googleapis/java-storage/issues/430)) ([38c1c34](https://www.github.com/googleapis/java-storage/commit/38c1c34937eeacd126cf6d62bf85fb9db90e1702))
* auto content-type on blob creation ([#338](https://www.github.com/googleapis/java-storage/issues/338)) ([66d1eb7](https://www.github.com/googleapis/java-storage/commit/66d1eb793383b9e83992824b392cedd28d54609f))
* expose updateTime field of the bucket ([#449](https://www.github.com/googleapis/java-storage/issues/449)) ([f0e945e](https://www.github.com/googleapis/java-storage/commit/f0e945e14662b86594298557b83151d3cb7e1ebb))


### Bug Fixes

* Ignore CONTRIBUTING.md ([#447](https://www.github.com/googleapis/java-storage/issues/447)) ([bdacdc9](https://www.github.com/googleapis/java-storage/commit/bdacdc93a107108add5bd9dc00473997534aa761)), closes [#446](https://www.github.com/googleapis/java-storage/issues/446) [#446](https://www.github.com/googleapis/java-storage/issues/446)
* PostPolicyV4 classes could be improved ([#442](https://www.github.com/googleapis/java-storage/issues/442)) ([8602b81](https://www.github.com/googleapis/java-storage/commit/8602b81eae95868e184fd4ab290396707bd21a8e))
* **docs:** example of Storage#testIamPermissions ([#434](https://www.github.com/googleapis/java-storage/issues/434)) ([275f452](https://www.github.com/googleapis/java-storage/commit/275f452a5993f95a84fb603a5f4b436238b39439))
* PostPolicyV4.PostFieldsV4.Builder.addCustomMetadataField() allows to add prefixed an not prefixed custom fields ([#398](https://www.github.com/googleapis/java-storage/issues/398)) ([02dc3b5](https://www.github.com/googleapis/java-storage/commit/02dc3b5e5377d8848c889647e72102cd9acc646d))


### Dependencies

* update dependency com.google.api-client:google-api-client to v1.30.10 ([#423](https://www.github.com/googleapis/java-storage/issues/423)) ([fbfa9ec](https://www.github.com/googleapis/java-storage/commit/fbfa9ecf277794e07d9a3c46d5b5022f54c37afd))
* update dependency com.google.api.grpc:grpc-google-cloud-kms-v1 to v0.86.1 ([#463](https://www.github.com/googleapis/java-storage/issues/463)) ([cf94230](https://www.github.com/googleapis/java-storage/commit/cf94230a5f02dcc16e364aa528d97046d80f59a0))
* update dependency com.google.api.grpc:proto-google-cloud-kms-v1 to v0.86.1 ([#464](https://www.github.com/googleapis/java-storage/issues/464)) ([6c372fa](https://www.github.com/googleapis/java-storage/commit/6c372fa81e49ac74bdda6f9b10914fac42767247))
* update dependency com.google.apis:google-api-services-storage to v1-rev20200611-1.30.10 ([#428](https://www.github.com/googleapis/java-storage/issues/428)) ([6ef57eb](https://www.github.com/googleapis/java-storage/commit/6ef57ebc9eeddc90f13ef87274e8ab0b7eb53290))
* update dependency com.google.apis:google-api-services-storage to v1-rev20200727-1.30.10 ([#457](https://www.github.com/googleapis/java-storage/issues/457)) ([edfd1e6](https://www.github.com/googleapis/java-storage/commit/edfd1e69e886adb04b98b54b3a63768c7e82b1e0))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.4 ([#452](https://www.github.com/googleapis/java-storage/issues/452)) ([12bc02d](https://www.github.com/googleapis/java-storage/commit/12bc02d7bc05e584cad4362628155333630fbcba))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.6 ([#458](https://www.github.com/googleapis/java-storage/issues/458)) ([f8d6e15](https://www.github.com/googleapis/java-storage/commit/f8d6e158a06aec926fb7bc42f10483d56696a37e))

### [1.111.2](https://www.github.com/googleapis/java-storage/compare/v1.111.1...v1.111.2) (2020-07-10)


### Dependencies

* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.3 ([#425](https://www.github.com/googleapis/java-storage/issues/425)) ([727b173](https://www.github.com/googleapis/java-storage/commit/727b1739963f5dc86009587eeb998d20adb94448))

### [1.111.1](https://www.github.com/googleapis/java-storage/compare/v1.111.0...v1.111.1) (2020-07-01)


### Dependencies

* update dependency com.google.apis:google-api-services-storage to v1-rev20200611-1.30.9 ([#406](https://www.github.com/googleapis/java-storage/issues/406)) ([b2ebea7](https://www.github.com/googleapis/java-storage/commit/b2ebea7a8fa0a2b2a2696c33da5f54a94b0f3d62))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.2 ([#414](https://www.github.com/googleapis/java-storage/issues/414)) ([4451887](https://www.github.com/googleapis/java-storage/commit/4451887bc58cdfa14488efcba6ad4040819ab71c))
* update dependency google-cloud-shared-config to v0.9.0 ([#417](https://www.github.com/googleapis/java-storage/issues/417))
* update dependency grpc-google-cloud-kms-v1 to v0.86.0 ([#417](https://www.github.com/googleapis/java-storage/issues/417))
* update dependency proto-google-cloud-kms-v1 to v0.86.0 ([#417](https://www.github.com/googleapis/java-storage/issues/417))


## [1.111.0](https://www.github.com/googleapis/java-storage/compare/v1.110.0...v1.111.0) (2020-06-25)


### Features

* add storage.upload(path) ([#269](https://www.github.com/googleapis/java-storage/issues/269)) ([9457f3a](https://www.github.com/googleapis/java-storage/commit/9457f3a76ff18552adc5f9c82f62ab8f3c207d31))
* Add support to disable logging from bucket ([#390](https://www.github.com/googleapis/java-storage/issues/390)) ([be72027](https://www.github.com/googleapis/java-storage/commit/be72027b1587b9b0a3e9e65e7a2231bdb2ae521f))
* expose all the methods of notification ([#141](https://www.github.com/googleapis/java-storage/issues/141)) ([8dfc0cb](https://www.github.com/googleapis/java-storage/commit/8dfc0cbf8294a7fc426948e22e5c2182da97b630))


### Reverts

* Revert "feat: expose all the methods of notification (#141)" (#393) ([3e02b9c](https://www.github.com/googleapis/java-storage/commit/3e02b9c4ee1ce0fb785d15b04bd36754e31831a0)), closes [#141](https://www.github.com/googleapis/java-storage/issues/141) [#393](https://www.github.com/googleapis/java-storage/issues/393)

## [1.110.0](https://www.github.com/googleapis/java-storage/compare/v1.109.1...v1.110.0) (2020-06-18)


### Features

* delete bucket OLM rules ([#352](https://www.github.com/googleapis/java-storage/issues/352)) ([0a528c6](https://www.github.com/googleapis/java-storage/commit/0a528c6916f8b031916a4c6ecc96ce5e49ea99c7))

### [1.109.1](https://www.github.com/googleapis/java-storage/compare/v1.109.0...v1.109.1) (2020-06-15)


### Dependencies

* bump shared-deps version and add back certain test deps ([#340](https://www.github.com/googleapis/java-storage/issues/340)) ([afd0339](https://www.github.com/googleapis/java-storage/commit/afd0339c1d62dfb82032e08e3ef50a14c80ad30a))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.1 ([#368](https://www.github.com/googleapis/java-storage/issues/368)) ([ccaf480](https://www.github.com/googleapis/java-storage/commit/ccaf48015f9e99fa2ee3b457eb5c04ad07c3253a))

## [1.109.0](https://www.github.com/googleapis/java-storage/compare/v1.108.0...v1.109.0) (2020-06-11)


### Features

* adopt flatten-maven-plugin and java-shared-dependencies ([#325](https://www.github.com/googleapis/java-storage/issues/325)) ([209cae3](https://www.github.com/googleapis/java-storage/commit/209cae322932a4f87729fe4c5176a4f11962cfae))
* stub implementation of StorageRpc for the sake of testing ([#351](https://www.github.com/googleapis/java-storage/issues/351)) ([dd58025](https://www.github.com/googleapis/java-storage/commit/dd5802555eb0351a5afa2f2f197cb93ca6d3b66e))


### Bug Fixes

* blob.reload() does not work as intuitively expected ([#308](https://www.github.com/googleapis/java-storage/issues/308)) ([a2bab58](https://www.github.com/googleapis/java-storage/commit/a2bab58ccd89f48e8d4a8ee2dd776b201598420d))


### Documentation

* **fix:** update client documentation link ([#324](https://www.github.com/googleapis/java-storage/issues/324)) ([eb8940c](https://www.github.com/googleapis/java-storage/commit/eb8940cc6a88b5e2b3dea8d0ab2ffc1e350ab924))
* Add doc for equals method in blob ([#311](https://www.github.com/googleapis/java-storage/issues/311)) ([91fc36a](https://www.github.com/googleapis/java-storage/commit/91fc36a6673e30d1cfa8c4da51b874e1fd0b0535))
* catch actual exception in java doc comment ([#312](https://www.github.com/googleapis/java-storage/issues/312)) ([9201de5](https://www.github.com/googleapis/java-storage/commit/9201de559fe4218abd2e4fac47beac62454547cf)), closes [#309](https://www.github.com/googleapis/java-storage/issues/309)
* update CONTRIBUTING.md to include code formatting ([#534](https://www.github.com/googleapis/java-storage/issues/534)) ([#315](https://www.github.com/googleapis/java-storage/issues/315)) ([466d08f](https://www.github.com/googleapis/java-storage/commit/466d08f9835a0f1dd00b5c9b3a08551be68d03ad))
* update readme to point client libarary documentation ([#317](https://www.github.com/googleapis/java-storage/issues/317)) ([8650f80](https://www.github.com/googleapis/java-storage/commit/8650f806736beec7bf7ab09a337b333bbf144f7b))


### Dependencies

* update dependency com.google.api.grpc:proto-google-common-protos to v1.18.0 ([#301](https://www.github.com/googleapis/java-storage/issues/301)) ([ff2dee2](https://www.github.com/googleapis/java-storage/commit/ff2dee2ce41d37787f0866ae740d3cd7f3b2bbd6))
* update dependency com.google.apis:google-api-services-storage to v1-rev20200410-1.30.9 ([#296](https://www.github.com/googleapis/java-storage/issues/296)) ([2e55aa2](https://www.github.com/googleapis/java-storage/commit/2e55aa2c8b9c78df9eebfe748fe72dcaae63ff81))
* update dependency com.google.apis:google-api-services-storage to v1-rev20200430-1.30.9 ([#319](https://www.github.com/googleapis/java-storage/issues/319)) ([3d03fa3](https://www.github.com/googleapis/java-storage/commit/3d03fa3381cfbb76d1501ec3d2ad14742a8a58dd))
* update dependency com.google.cloud:google-cloud-conformance-tests to v0.0.11 ([#320](https://www.github.com/googleapis/java-storage/issues/320)) ([6c18c88](https://www.github.com/googleapis/java-storage/commit/6c18c882cfe0c35b310a518e6044847e6fbeab94))

## [1.108.0](https://www.github.com/googleapis/java-storage/compare/v1.107.0...v1.108.0) (2020-04-30)


### Features

* add mockito dependency ([#284](https://www.github.com/googleapis/java-storage/issues/284)) ([58692dd](https://www.github.com/googleapis/java-storage/commit/58692dd8eeb2d228d14c896e563184d723b25df1))
* V4 POST policy ([#177](https://www.github.com/googleapis/java-storage/issues/177)) ([32d8ffa](https://www.github.com/googleapis/java-storage/commit/32d8fface1a994cb5ac928f08c0467edc3c9aab1))


### Bug Fixes

* Documentation for Blob.update() and Storage.update() methods is confusing/incorrect ([#261](https://www.github.com/googleapis/java-storage/issues/261)) ([876405f](https://www.github.com/googleapis/java-storage/commit/876405f81cf195f5619b353be8d1e8efcbf5e0b3)), closes [#252](https://www.github.com/googleapis/java-storage/issues/252)


### Dependencies

* pin mockito version to work with java 7 ([#292](https://www.github.com/googleapis/java-storage/issues/292)) ([8eb2fff](https://www.github.com/googleapis/java-storage/commit/8eb2fff3f51c90af7f76f74d40ed1d6d6b4320b7))
* update dependency com.google.api.grpc:grpc-google-cloud-kms-v1 to v0.85.1 ([#273](https://www.github.com/googleapis/java-storage/issues/273)) ([7b5e7d1](https://www.github.com/googleapis/java-storage/commit/7b5e7d173cdac6b2de802c568e3a60b915d39d1c))
* update dependency com.google.api.grpc:proto-google-cloud-kms-v1 to v0.85.1 ([#274](https://www.github.com/googleapis/java-storage/issues/274)) ([0ab4304](https://www.github.com/googleapis/java-storage/commit/0ab4304ea4e5e5668c05c67d2c96c6056f8c19c2))
* update dependency com.google.cloud:google-cloud-conformance-tests to v0.0.10 ([#281](https://www.github.com/googleapis/java-storage/issues/281)) ([f3dee7e](https://www.github.com/googleapis/java-storage/commit/f3dee7ea0d0e305f0bc0c980aa65e538f7bf890c))
* update dependency com.google.http-client:google-http-client-bom to v1.35.0 ([#282](https://www.github.com/googleapis/java-storage/issues/282)) ([1c1c1be](https://www.github.com/googleapis/java-storage/commit/1c1c1bee0d6382e76e74f9a00dca8e527cc390c6))
* update dependency io.grpc:grpc-bom to v1.28.1 ([#250](https://www.github.com/googleapis/java-storage/issues/250)) ([b35e81c](https://www.github.com/googleapis/java-storage/commit/b35e81ce19fa72672aefe8bd956959bfa954194c))
* update dependency io.grpc:grpc-bom to v1.29.0 ([#275](https://www.github.com/googleapis/java-storage/issues/275)) ([9b241b4](https://www.github.com/googleapis/java-storage/commit/9b241b468d4f3a73b81c5bc67c085c6fe7c6ea1e))
* update dependency org.threeten:threetenbp to v1.4.4 ([#278](https://www.github.com/googleapis/java-storage/issues/278)) ([7bae49f](https://www.github.com/googleapis/java-storage/commit/7bae49f16ba5de0eeac8301a6a11b85bd4406ed5))


### Documentation

* label legacy storage classes in documentation ([#267](https://www.github.com/googleapis/java-storage/issues/267)) ([50e5938](https://www.github.com/googleapis/java-storage/commit/50e5938147f7bb2594b9a142e8087c6e555f4979)), closes [#254](https://www.github.com/googleapis/java-storage/issues/254)

## [1.107.0](https://www.github.com/googleapis/java-storage/compare/v1.106.0...v1.107.0) (2020-04-14)


### Bug Fixes

* Blob API Doc is confusing ([#233](https://www.github.com/googleapis/java-storage/issues/233)) ([b5208b8](https://www.github.com/googleapis/java-storage/commit/b5208b87e5469bfdf684bd5f250921be99a59ac8))
* Blob.downloadTo() methods do not wrap RetryHelper$RetryHelperException ([#218](https://www.github.com/googleapis/java-storage/issues/218)) ([5599f29](https://www.github.com/googleapis/java-storage/commit/5599f299018cb363d600d4e39e35d2657b74f5bc))
* implementations of FromHexString() for md5 and crc32c ([#246](https://www.github.com/googleapis/java-storage/issues/246)) ([c9b23b3](https://www.github.com/googleapis/java-storage/commit/c9b23b36874211681ea323ef89a69316438924af))
* storage-client-lib-docs to right location ([#213](https://www.github.com/googleapis/java-storage/issues/213)) ([133d137](https://www.github.com/googleapis/java-storage/commit/133d1377781fd6bdc58dd4f494a75ec1d7b9e530))
* surface storage interface expectations correctly. ([#241](https://www.github.com/googleapis/java-storage/issues/241)) ([130a641](https://www.github.com/googleapis/java-storage/commit/130a6413abbc1eacd0ee5c10dbbba699e1f528ea))
* throw io exception instead of storage exception ([#229](https://www.github.com/googleapis/java-storage/issues/229)) ([4d42a4e](https://www.github.com/googleapis/java-storage/commit/4d42a4eb1feb2afc6a6a9f3a3797b33f33f50900))


### Reverts

* Revert "feat: add upload functionality (#214)" (#224) ([e87c731](https://www.github.com/googleapis/java-storage/commit/e87c7319c610454c9e7e052d0a4a4e7454e4d9a4)), closes [#214](https://www.github.com/googleapis/java-storage/issues/214) [#224](https://www.github.com/googleapis/java-storage/issues/224)
* grpc version update ([#248](https://www.github.com/googleapis/java-storage/issues/248)) ([0f6703e](https://www.github.com/googleapis/java-storage/commit/0f6703ea2d8374667728ebcb4c398c6681280c58))


### Dependencies

* update conformance test dep ([#210](https://www.github.com/googleapis/java-storage/issues/210)) ([010c112](https://www.github.com/googleapis/java-storage/commit/010c1128761d9c74ba1af33bc34e9264f34b8c80))
* update core dependencies ([#182](https://www.github.com/googleapis/java-storage/issues/182)) ([3f0c59c](https://www.github.com/googleapis/java-storage/commit/3f0c59c18ecfd844f718346768dc274a9e2f131d))
* update core dependencies to v1.93.4 ([#231](https://www.github.com/googleapis/java-storage/issues/231)) ([1bb5787](https://www.github.com/googleapis/java-storage/commit/1bb578710148bab21c978e31b00608f7f9770128))
* update dependency com.google.api:api-common to v1.9.0 ([#209](https://www.github.com/googleapis/java-storage/issues/209)) ([789ceaa](https://www.github.com/googleapis/java-storage/commit/789ceaa2be6163f85f483637205191e38029e0c2))
* update dependency com.google.api.grpc:grpc-google-cloud-kms-v1 to v0.85.0 ([#222](https://www.github.com/googleapis/java-storage/issues/222)) ([03eace6](https://www.github.com/googleapis/java-storage/commit/03eace664dd13164c1db68b4895185d318d13d64))
* update dependency com.google.api.grpc:proto-google-cloud-kms-v1 to v0.85.0 ([#223](https://www.github.com/googleapis/java-storage/issues/223)) ([aaf6a17](https://www.github.com/googleapis/java-storage/commit/aaf6a1728a9dd7e0bde1b6f52dd628c020cb73d3))
* update dependency com.google.apis:google-api-services-storage to v1-rev20200326-1.30.9 ([#239](https://www.github.com/googleapis/java-storage/issues/239)) ([b9d0a70](https://www.github.com/googleapis/java-storage/commit/b9d0a70c2a9ca1febafd1c1b8699c25e9e30e9b2))
* update dependency com.google.cloud.samples:shared-configuration to v1.0.14 ([#207](https://www.github.com/googleapis/java-storage/issues/207)) ([be74072](https://www.github.com/googleapis/java-storage/commit/be74072662f2e3a99e54ee3d3feff66cb39032b2))
* update dependency com.google.guava:guava to v29 ([#240](https://www.github.com/googleapis/java-storage/issues/240)) ([7824c15](https://www.github.com/googleapis/java-storage/commit/7824c15ab38ad89111c3eb9e77a499479a62742b))
* update dependency org.threeten:threetenbp to v1.4.2 ([#200](https://www.github.com/googleapis/java-storage/issues/200)) ([84faad1](https://www.github.com/googleapis/java-storage/commit/84faad1a854c3a189d2997a121a8753988213f90))
* update dependency org.threeten:threetenbp to v1.4.3 ([#228](https://www.github.com/googleapis/java-storage/issues/228)) ([be40a70](https://www.github.com/googleapis/java-storage/commit/be40a70fbe2d1556d26c7983c5ad62535ce6dfbd))


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
