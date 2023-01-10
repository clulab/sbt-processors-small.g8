package $package$.apps

import org.clulab.dynet.Utils
import org.clulab.processors.Document
import org.clulab.processors.clu.CluProcessor
import org.clulab.struct.DirectedGraphEdgeIterator

import $package$.$class$
import $package$.common.utils.$class$App

object ProcessorsApp extends $class$App {
  val appMessage = args.lift(0).getOrElse(getArgString("apps.ProcessorsApp.message", Some("ProcessorsApp message not found!")))
  logger.info(appMessage)

  val $class;format="decap"$ = $class$()
  val classMessage = $class;format="decap"$.getArgString("$class$.message", Some("Class message not found!"))
  logger.info(classMessage)

  // Create the processor.  Any processor works here!
  Utils.initializeDyNet()
  val processor = new CluProcessor()
  // The actual work is done here.
  val document = processor.annotate("John Smith went to China. He visited Beijing on January 10th, 2013.")

  // You are basically done.  The rest of this code simply prints out the annotations.

  // Let's print the sentence-level annotations.
  for ((sentence, sentenceIndex) <- document.sentences.zipWithIndex) {
    println("Sentence #" + sentenceIndex + ":")
    println("Tokens: " + mkString(sentence.words))
    println("Start character offsets: " + mkString(sentence.startOffsets))
    println("End character offsets: " + mkString(sentence.endOffsets))

    // These annotations are optional, so they are stored using Option objects,
    // hence the foreach statement.
    sentence.lemmas.foreach(lemmas => println("Lemmas: " + mkString(lemmas)))
    sentence.tags.foreach(tags => println("POS tags: " + mkString(tags)))
    sentence.chunks.foreach(chunks => println("Chunks: " + mkString(chunks)))
    sentence.entities.foreach(entities => println("Named entities: " + mkString(entities)))
    sentence.norms.foreach(norms => println("Normalized entities: " + mkString(norms)))
    sentence.dependencies.foreach { dependencies =>
      println("Syntactic dependencies:")
      val iterator = new DirectedGraphEdgeIterator[String](dependencies)
      iterator.foreach { dep =>
        // Note that we use offsets starting at 0 unlike CoreNLP, which uses offsets starting at 1.
        println(" head: " + dep._1 + " modifier: " + dep._2 + " label: " + dep._3)
      }
    }
    sentence.syntacticTree.foreach { syntacticTree =>
      // See the org.clulab.utils.Tree class for more information
      // on syntactic trees, including access to head phrases/words.
      println("Constituent tree: " + syntacticTree)
    }
    println()
    println()
  }

  // Let's print the coreference chains.
  document.coreferenceChains.foreach { chains =>
    for (chain <- chains.getChains) {
      println("Found one coreference chain containing the following mentions:")
      for (mention <- chain) {
        val text = document.sentences(mention.sentenceIndex).words
            .slice(mention.startOffset, mention.endOffset).mkString("[", " ", "]")
        // Note that all these offsets start at 0, too.
        println("\tsentenceIndex: " + mention.sentenceIndex +
            " headIndex: " + mention.headIndex +
            " startTokenOffset: " + mention.startOffset +
            " endTokenOffset: " + mention.endOffset +
            " text: " + text)
      }
    }
  }

  def mkString[T](elems: Array[T]): String = elems.mkString(" ")
}
