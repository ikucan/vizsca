//package ik.util.arrow
//
//import java.io.File
//import java.io.FileInputStream
//
//import org.apache.arrow.memory.RootAllocator
//import org.apache.arrow.vector.ipc.{ArrowFileReader, SeekableReadChannel}
//
//
//object arrw_test {
//
//  def main(argv: Array[String]): Unit = {
//
//    println(123)
//    val fis = new FileInputStream("c:/tmp/dud.parq")
//
//    val chnnl = new SeekableReadChannel(fis.getChannel)
//    val arr_rdr = new ArrowFileReader(chnnl, new RootAllocator(Integer.MAX_VALUE))
//    val root = arr_rdr.getVectorSchemaRoot(); // get root
//    val schema = root.getSchema(); // get schema
//
//    val i = 3
//
//
//  }
//}
