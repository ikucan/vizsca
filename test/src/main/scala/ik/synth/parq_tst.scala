package ik.synth

import org.apache.parquet.column.page.PageReadStore
import org.apache.parquet.example.data.Group
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter
import org.apache.parquet.format.converter.ParquetMetadataConverter
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.metadata.ParquetMetadata
import org.apache.parquet.io.ColumnIOFactory
import org.apache.parquet.io.MessageColumnIO
import org.apache.parquet.io.RecordReader
import org.apache.parquet.schema.MessageType
import org.apache.parquet.schema.Type

object parq_tst {


  def main(argv: Array[String]): Unit = {
    val fnm = "/workstem/py_tst/tst2.parquet"


  }
}