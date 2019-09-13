package com.apifortress.afthem.exceptions

import com.apifortress.afthem.messages.WebParsedRequestMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito._

class ExceptionsTests {

  @Test
  def testBackendConfigurationMissingException : Unit = {
    assertEquals("No backend configuration found",new BackendConfigurationMissingException().getMessage)
  }

  @Test
  def testGenericException : Unit = {
    assertEquals("Generic exception during : foobar",new GenericException("foobar").getMessage)
  }

  @Test
  def testRejectedRequestException : Unit = {
    val message = mock(classOf[WebParsedRequestMessage])
    assertEquals("Request has been rejected",new RejectedRequestException(message).getMessage)
  }

  @Test
  def testUnauthorizedException : Unit = {
    val message = mock(classOf[WebParsedRequestMessage])
    assertEquals("API Key not authorized",new UnauthorizedException(message).getMessage)
  }

  @Test
  def testAfhtemFlowException : Unit = {
    val message = mock(classOf[WebParsedRequestMessage])
    assertEquals("foobar",new AfthemFlowException(message,"foobar").getMessage)
  }
}
