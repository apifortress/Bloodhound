package com.apifortress.afthem

import com.apifortress.afthem.messages.beans.{Header, HttpWrapper}

object TestData {
  def createWrapper() : HttpWrapper =
    new HttpWrapper("http://foo.com",200,"GET",
      List[Header](new Header(ReqResUtil.HEADER_CONTENT_LENGTH,"123"),
        new Header(ReqResUtil.HEADER_CONTENT_ENCODING,"gzip"),
        new Header("x-key","ABC123")))
}
