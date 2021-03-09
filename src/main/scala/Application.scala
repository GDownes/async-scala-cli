import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.slf4j.LoggerFactory
import dispatch._
import Defaults._

object Application extends App {
  val logger = LoggerFactory.getLogger(getClass.getSimpleName)

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  logger.info("Getting latest tender ids")

  val tendersResponse = Http.default(url("https://api.tender-ni.com/tender") OK as.String)
  tendersResponse.map(tendersResponse => {
    val tenders = mapper.readValue(tendersResponse, new TypeReference[List[Map[String, String]]] {})
    val tenderDescriptions = tenders.map(tender => getTenderDescription(tender("id")))
    Future.sequence(tenderDescriptions).foreach(tenderDescriptions => println(tenderDescriptions))
  })

  def getTenderDescription(id: String): Future[String] = {
    logger.info(s"Getting tender details for tender id $id")
    val tenderResponse = Http.default(url(s"https://api.tender-ni.com/tender/$id") OK as.String)
    tenderResponse.map(tenderResponse => {
      val tender = mapper.readValue(tenderResponse, new TypeReference[Map[String, Object]] {})
      tender("description").toString
    })
  }
}


