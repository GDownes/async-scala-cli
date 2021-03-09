import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.slf4j.LoggerFactory
import scalaj.http.Http

object Application extends App {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val logger = LoggerFactory.getLogger(getClass.getSimpleName)
  logger.info("Getting latest tender ids")
  val tendersResponse = Http("https://api.tender-ni.com/tender").asString
  val tenders = mapper.readValue(tendersResponse.body, new TypeReference[List[Map[String, String]]] {})
  val tenderDescriptions = tenders.map(tender => getTenderDescription(tender("id")))
  println(tenderDescriptions)

  def getTenderDescription(id: String): String = {
    logger.info("Getting tender details for tender id " + id)
    val tenderResponse = Http(s"https://api.tender-ni.com/tender/${id}").asString
    val tenderDetails = mapper.readValue(tenderResponse.body, new TypeReference[Map[String, Object]] {})
    tenderDetails.getOrElse("description", "Unknown").toString
  }
}


