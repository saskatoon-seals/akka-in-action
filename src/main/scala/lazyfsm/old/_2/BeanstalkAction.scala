package lazyfsm.old._2

import lazyfsm.external.Beanstalk
import lazyfsm.external.Messages._

/*
 * Can I give it a parameter T so that T <: ConfiguredClient?
 *
 * I don't think so, because this would imply a heterogeneous list of actions.
 * This is mixing of compile-time and runtime type checking.
 */
trait BeanstalkAction {
  def run: Beanstalk => Unit
}

case class UploaderDelete(jobId: Int) extends BeanstalkAction {
  def run = _.delete(jobId)
}

case class UploaderSend(msg: Message) extends BeanstalkAction {
  def run = _.write(msg)
}

case class ArchiverSend(msg: Message) extends BeanstalkAction {
  def run = _.write(msg)
}

case object NoAction extends BeanstalkAction {
  def run = _ => ()
}
