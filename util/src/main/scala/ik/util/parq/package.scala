package ik.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.util.HadoopInputFile
import org.apache.parquet.io.ColumnIOFactory
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName
import scala.collection.JavaConverters._
import ik.util.df._

package object parq {

  class col_bldr[T <: Any](n: Int)(implicit m: Manifest[T]) {
    val typ_nme = m.toString
    val arr = new Array[T](n)
    private var idx: Int = 0

    def +=(value: T): Unit = {
      arr(idx) = value
      idx += 1
    }

    def to_arr: Array[T] = arr
  }

  def rd(fnm: String): frm = {
    val reader = ParquetFileReader.open(HadoopInputFile.fromPath(new Path(fnm), new Configuration))
    val schema = reader.getFooter.getFileMetaData.getSchema
    val fields = schema.getFields
    val n_records = reader.getRecordCount.toInt
    val names = fields.asScala.toArray map (_.getName)

    val typ_names = fields.asScala.toArray map (_.asPrimitiveType.getPrimitiveTypeName)

    val bldrs = (0 until typ_names.size) map { typ_idx =>
      typ_names(typ_idx) match {
        case PrimitiveTypeName.DOUBLE => new col_bldr[Double](n_records)
        case PrimitiveTypeName.INT64 => new col_bldr[Long](n_records)
        case PrimitiveTypeName.INT32 => new col_bldr[Int](n_records)
        case PrimitiveTypeName.FLOAT => new col_bldr[Float](n_records)
        case PrimitiveTypeName.BINARY => new col_bldr[Symbol](n_records)
        case x => throw new RuntimeException(raw"unhandled frame type:>> ${x}")
      }
    }

    var pages = reader.readNextRowGroup()
    while (pages != null) {
      val rows = pages.getRowCount();
      val columnIO = new ColumnIOFactory().getColumnIO(schema);
      val recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));

      for (i <- 0L until rows) {
        val simpleGroup = recordReader.read()

        for (col_idx <- 0 until typ_names.size) {
          typ_names(col_idx) match {
            case PrimitiveTypeName.DOUBLE => bldrs(col_idx).asInstanceOf[col_bldr[Double]] += simpleGroup.getDouble(col_idx, 0)
            case PrimitiveTypeName.INT64 => bldrs(col_idx).asInstanceOf[col_bldr[Long]] += simpleGroup.getLong(col_idx, 0)
            case PrimitiveTypeName.INT32 => bldrs(col_idx).asInstanceOf[col_bldr[Int]] += simpleGroup.getInteger(col_idx, 0)
            case PrimitiveTypeName.FLOAT => bldrs(col_idx).asInstanceOf[col_bldr[Float]] += simpleGroup.getFloat(col_idx, 0)
            case PrimitiveTypeName.BINARY => bldrs(col_idx).asInstanceOf[col_bldr[Symbol]] += Symbol(simpleGroup.getBinary(col_idx, 0).toStringUsingUTF8)
            case x => throw new RuntimeException(raw"unhandled frame type:>> ${x}")
          }
        }
      }
      pages = reader.readNextRowGroup()
    }
    reader.close();

    val df = new frm()
    for (col_idx <- 0 until typ_names.size) {
      if (!names(col_idx).startsWith("__index_")) {
        typ_names(col_idx) match {
          case PrimitiveTypeName.DOUBLE => df.set_arg(names(col_idx), bldrs(col_idx).asInstanceOf[col_bldr[Double]].arr)
          case PrimitiveTypeName.INT64 => df.set_arg(names(col_idx), bldrs(col_idx).asInstanceOf[col_bldr[Long]].arr)
          case PrimitiveTypeName.INT32 => df.set_arg(names(col_idx), bldrs(col_idx).asInstanceOf[col_bldr[Int]].arr)
          case PrimitiveTypeName.FLOAT => df.set_arg(names(col_idx), bldrs(col_idx).asInstanceOf[col_bldr[Float]].arr)
          case PrimitiveTypeName.BINARY => df.set_arg(names(col_idx), bldrs(col_idx).asInstanceOf[col_bldr[Symbol]].arr)
          case x => throw new RuntimeException(raw"unhandled frame type:>> ${x}")
        }
      }
    }
    df
  }
}
