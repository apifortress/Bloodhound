# AFtheM

AFtheM is an HTTP API Microgateway that acts are a reverse proxy.

The pillars of the project are:

* **Modularity**: the system should be expandable by the creation of modules. The activation of modules should not
  require recompilation of repackaging the software. The act of the creation of modules should require the very little
  knowledge of the inner workings of the software, according to our *No Esoteric Bullshit* policy.

* **Customization**: the user should be able to create various pipelines by connecting different steps from
  different modules, in order to achieve a certain goal without special boundaries dictated by the modules. If it's not
  illogical, it should be possible.

* **Fine tuning**: the system performance and resource usage should be fine-tunable by the user, according to their
  needs and knowledge of how the APIs they're proxying work.

* **With developers in mind**: the development process should always consider the ability to capture, debug and
  transform APIs the *main goal* of the project. The tool should be a valuable companion in the process of identifying
  flaws and weaknesses.
  
