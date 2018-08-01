package lazyfsm.external

import lazyfsm.external.Messages.Message

case class Beanstalk(tubeName: String) {
  //The constructor
  watch(tubeName)
  use(tubeName)

  //The methods
  def write(msg: Message): Unit = ???
  def read(): Message = ???

  def delete(jobId: Int): Unit = ???

  def watch(tubeName: String): Unit = ???
  def use(tubeName: String): Unit = ???
}
