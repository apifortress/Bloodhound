package com.apifortress.afthem.exceptions

import java.io.IOException

case class NoWorkingUpstreamsException() extends IOException("No working upstreams left")
