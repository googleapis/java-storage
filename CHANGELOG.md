# Changelog

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
