/**
 * Copyright (C) 2019 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expediagroup.beekeeper.cleanup.path.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.findify.s3mock.S3Mock;

import com.amazonaws.services.s3.AmazonS3;

@ExtendWith(MockitoExtension.class)
class S3SentinelFilesCleanerTest {

  private final String bucket = "bucket";
  private final String partition1Sentinel = "table/partition_1_$folder$";
  private final String partition1AbsolutePath = "s3://bucket/table/partition_1";
  private final String tableName = "table";

  private final S3Mock s3Mock = new S3Mock.Builder().withPort(0).withInMemoryBackend().build();
  private AmazonS3 amazonS3;
  private S3Client s3Client;
  private S3SentinelFilesCleaner s3SentinelFilesCleaner;

  @BeforeEach
  void setUp() {
    amazonS3 = AmazonS3Factory.newInstance(s3Mock);
    amazonS3.createBucket(bucket);
    s3Client = new S3Client(amazonS3, false);
    s3SentinelFilesCleaner = new S3SentinelFilesCleaner(s3Client);
  }

  @AfterEach
  void tearDown() {
    s3Mock.shutdown();
  }

  @Test
  void typical() {
    amazonS3.putObject(bucket, partition1Sentinel, "");
    s3SentinelFilesCleaner.deleteSentinelFiles(partition1AbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partition1Sentinel)).isFalse();
  }

  @Test
  void invalidS3Path() {
    String invalidS3AbsolutePath = "table/partition_1";
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> s3SentinelFilesCleaner.deleteSentinelFiles(invalidS3AbsolutePath))
        .withMessage("Invalid S3 URI: no hostname: %s", invalidS3AbsolutePath);
  }

  @Test
  void moreThanOneSentinelFile() {
    String partition11Sentinel = "table/partition_11_$folder$";
    amazonS3.putObject(bucket, partition11Sentinel, "");
    amazonS3.putObject(bucket, partition1Sentinel, "");

    s3SentinelFilesCleaner.deleteSentinelFiles(partition1AbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partition1Sentinel)).isFalse();
    assertThat(amazonS3.doesObjectExist(bucket, partition11Sentinel)).isTrue();
  }

  @Test
  void nonEmptySentinelFile() {
    amazonS3.putObject(bucket, partition1Sentinel, "content");
    s3SentinelFilesCleaner.deleteSentinelFiles(partition1AbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partition1Sentinel)).isTrue();
  }

  @Test
  void sentinelFileDoesntExist() {
    amazonS3.putObject(bucket, "table/partition_1", "content");
    assertThatCode(() -> s3SentinelFilesCleaner.deleteSentinelFiles(partition1AbsolutePath)).doesNotThrowAnyException();
  }

  @Test
  void sentinelFileForNonEmptyParent() {
    String partitionSentinel = "table/id1/partition_1_$folder$";
    String partitionParentSentinel = "table/id1_$folder$";
    String parentFile = "table/id1/randomFile";
    String partitionAbsolutePath = "s3://bucket/table/id1/partition_1";

    amazonS3.putObject(bucket, partitionSentinel, "");
    amazonS3.putObject(bucket, partitionParentSentinel, "");
    amazonS3.putObject(bucket, parentFile, "content");

    s3SentinelFilesCleaner.deleteSentinelFiles(partitionAbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partitionSentinel)).isFalse();
    assertThat(amazonS3.doesObjectExist(bucket, parentFile)).isTrue();
    assertThat(amazonS3.doesObjectExist(bucket, partitionParentSentinel)).isTrue();
  }

  @Test
  void sentinelFileForEmptyParentPathDoesNotContainTableName() {
    String partitionSentinel = "randomLocation/id1/partition_1_$folder$";
    String partitionParentSentinel = "randomLocation/id1_$folder$";
    String partitionAbsolutePath = "s3://bucket/randomLocation/id1/partition_1";

    amazonS3.putObject(bucket, partitionSentinel, "");
    amazonS3.putObject(bucket, partitionParentSentinel, "");

    s3SentinelFilesCleaner.deleteSentinelFiles(partitionAbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partitionSentinel)).isFalse();
    assertThat(amazonS3.doesObjectExist(bucket, partitionParentSentinel)).isTrue();
  }

  @Test
  void sentinelFileForEmptyParentPathHasTableNamePrefix() {
    String tableTest = tableName + "Test";
    String partitionSentinel = tableTest + "/id1/partition_1_$folder$";
    String partitionParentSentinel = tableTest + "/id1_$folder$";
    String partitionAbsolutePath = "s3://bucket/" + tableTest + "/id1/partition_1";

    amazonS3.putObject(bucket, partitionSentinel, "");
    amazonS3.putObject(bucket, partitionParentSentinel, "");

    s3SentinelFilesCleaner.deleteSentinelFiles(partitionAbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partitionSentinel)).isFalse();
    assertThat(amazonS3.doesObjectExist(bucket, partitionParentSentinel)).isTrue();
  }

  @Test
  void sentinelFileForEmptyParentPathHasTableNameSuffix() {
    String testTable = "test" + tableName;
    String partitionSentinel = testTable + "/id1/partition_1_$folder$";
    String partitionParentSentinel = testTable + "/id1_$folder$";
    String partitionAbsolutePath = "s3://bucket/" + testTable + "/id1/partition_1";

    amazonS3.putObject(bucket, partitionSentinel, "");
    amazonS3.putObject(bucket, partitionParentSentinel, "");

    s3SentinelFilesCleaner.deleteSentinelFiles(partitionAbsolutePath);
    assertThat(amazonS3.doesObjectExist(bucket, partitionSentinel)).isFalse();
    assertThat(amazonS3.doesObjectExist(bucket, partitionParentSentinel)).isTrue();
  }
}
