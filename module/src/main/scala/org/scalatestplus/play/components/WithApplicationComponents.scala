package org.scalatestplus.play.components

import play.api.ApplicationLoader.Context
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{ BuiltInComponents, _ }

/**
 * A trait that provides a components in scope and creates new components when newApplication is called.
 *
 * Mixin one of the public traits in this file to provide the desired functionality.
 *
 * This class has several methods that can be used to customize the behavior in specific ways.
 *
 * This is targeted at functional tests requiring a running application that is bootstrapped using Macwire/Compile time DI.
 * This is provided as an alternative to the [[GuiceApplicationBuilder]] which requires guice bootstrapping.
 *
 * @see https://www.playframework.com/documentation/2.5.x/ScalaFunctionalTestingWithScalaTest#Creating-Application-instances-for-testing
 * @tparam C the type of the fully-built components class
 */
trait WithApplicationComponents[C <: BuiltInComponents] {
  private var _components: C = _

  /**
   * @return The current components
   */
  final def components: C = _components

  /**
   * Override this function to in your test to instantiate the components - a factory of sorts.
   *
   * For example:
   *
   * <pre class="stHighlight">
   * override def createComponents(context: Context): MyComponents = new MyComponents(context)
   * </pre>
   *
   * @return the components to be used by the application
   */
  def createComponents(context: Context): C

  /**
   * @return new application instance and set the components. This must be called for components to be properly set up.
   */
  final def newApplication: Application = {
    _components = createComponents(context)
    initialize(_components)
  }

  /**
   * Initialize the application from the components. This can be used to do eager instantiation or otherwise
   * set up things.
   *
   * @return the application that will be used for testing
   */
  def initialize(components: C): Application = _components.application

  /**
   * @return a context to use to create the application.
   */
  def context: ApplicationLoader.Context = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    ApplicationLoader.createContext(env)
  }
}
