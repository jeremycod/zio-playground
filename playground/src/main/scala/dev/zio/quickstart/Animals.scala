package dev.zio.quickstart

object Animals extends App {
  object Species extends Enumeration {
    val CAT, DOG, BIRD, FISH = Value
  }
  case class Specie(specieType: Species.Value, name: String, owner: String, location: String)

  val specie = Specie(Species.CAT, "testC", "test", "test")
  val specie2 = Specie(Species.DOG, "testD", "test", "test")
  val specie3 = Specie(Species.BIRD, "testB", "test", "test")
  val specie4 = Specie(Species.FISH, "testF", "test", "test")

  val animals = Seq(specie, specie2, specie3, specie4)



  trait Animal

  case class Cat(name: String , owner: String , location: String, catSpecific: String) extends Animal
  case class Dog(name: String , owner: String , location: String, dogSpecific: String ) extends Animal
  case class Bird(name: String , owner: String , location: String, birdSpecific: String ) extends Animal
  case class Fish(name: String , owner: String , location: String, fishSpecific: String ) extends Animal


  def toAnimal(specie: Specie): Animal = {
    val constructors: Map[Species.Value, (String, String, String) => Animal] = Map(
      Species.CAT -> Cat.apply,
      Species.DOG -> Dog.apply,
      Species.BIRD -> Bird.apply,
      Species.FISH -> Fish.apply
    )

    constructors(specie.specieType)(specie.name, specie.owner, specie.location)
  }

  val myAnimals: Seq[Animal] = animals.map(toAnimal)
  println(myAnimals)




}
