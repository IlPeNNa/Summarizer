CREATE DATABASE  IF NOT EXISTS `summarizerdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `summarizerdb`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: summarizerdb
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `summary_id` int NOT NULL,
  `user_id` int NOT NULL,
  `rating` int NOT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_summary_feedback` (`user_id`,`summary_id`),
  KEY `idx_feedback_summary` (`summary_id`,`deleted_at`),
  CONSTRAINT `fk_feedback_summary` FOREIGN KEY (`summary_id`) REFERENCES `summaries` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,2,2,5,'Perfetto','2026-02-11 09:39:26',NULL),(2,3,2,4,'buono','2026-02-11 11:22:30',NULL);
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `summaries`
--

DROP TABLE IF EXISTS `summaries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `summaries` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `original_text` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `summary_text` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_length` int NOT NULL,
  `summary_length` int NOT NULL,
  `word_count` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_summaries` (`user_id`,`deleted_at`,`created_at` DESC),
  CONSTRAINT `fk_summary_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `summaries`
--

LOCK TABLES `summaries` WRITE;
/*!40000 ALTER TABLE `summaries` DISABLE KEYS */;
INSERT INTO `summaries` VALUES (1,2,'Brunson was the first player to win $1 million in poker tournaments.[5] He won ten WSOP bracelets throughout his career, tied with Johnny Chan and Erik Seidel for third all time, behind Phil Hellmuth\'s seventeen and Phil Ivey\'s eleven.[6] He is also one of only four players to have won the Main Event at the World Series of Poker multiple times, which he did in 1976 and 1977. He is also one of only three players, along with Bill Boyd and Loren Klein, to have won WSOP tournaments in four consecutive years. In addition, he is the first of six players to win both the WSOP Main Event and a World Poker Tour title. In January 2006, Bluff magazine voted Brunson the most influential force in the world of poker.[7]','Brunson was the first player to win $1 million in Poker tournaments. He won ten WSOP bracelets throughout his career, tied with Johnny Chan and Erik Seidel for third all time. He is one of only four players to have won the Main Event at the World Series of Poker multiple times, which he did in 1976 and 1977.',714,309,59,'2026-02-11 09:35:22','2026-02-11 09:53:19'),(2,2,'Brunson was the first player to win $1 million in poker tournaments.[5] He won ten WSOP bracelets throughout his career, tied with Johnny Chan and Erik Seidel for third all time, behind Phil Hellmuth\'s seventeen and Phil Ivey\'s eleven.[6] He is also one of only four players to have won the Main Event at the World Series of Poker multiple times, which he did in 1976 and 1977. He is also one of only three players, along with Bill Boyd and Loren Klein, to have won WSOP tournaments in four consecutive years. In addition, he is the first of six players to win both the WSOP Main Event and a World Poker Tour title. In January 2006, Bluff magazine voted Brunson the most influential force in the world of poker.[7]','Brunson was the first player to win $1 million in Poker tournaments. He won ten WSOP bracelets throughout his career, tied with Johnny Chan and Erik Seidel for third all time. He is one of only four players to have won the Main Event at the World Series of Poker multiple times, which he did in 1976 and 1977.',714,309,59,'2026-02-11 09:39:00',NULL),(3,2,'Proceedings of the 58th Annual Meeting of the Association for Computational Linguistics , pages 7871–7880\nJuly 5 - 10, 2020. c\n2020 Association for Computational Linguistics7871BART: Denoising Sequence-to-Sequence Pre-training for Natural\nLanguage Generation, Translation, and Comprehension\nMike Lewis*, Yinhan Liu*, Naman Goyal*, Marjan Ghazvininejad,\nAbdelrahman Mohamed, Omer Levy, Ves Stoyanov, Luke Zettlemoyer\nFacebook AI\nmikelewis@fb.com,yinhan@ai2incubator.com,naman@fb.com\nAbstract\nWe present BART, a denoising autoencoder\nfor pretraining sequence-to-sequence models.\nBART is trained by (1) corrupting text with an\narbitrary noising function, and (2) learning a\nmodel to reconstruct the original text. It uses\na standard Tranformer-based neural machine\ntranslation architecture which, despite its sim-\nplicity, can be seen as generalizing BERT (due\nto the bidirectional encoder), GPT (with the\nleft-to-right decoder), and other recent pre-\ntraining schemes. We evaluate a number of\nnoising approaches, ﬁnding the best perfor-\nmance by both randomly shufﬂing the order of\nsentences and using a novel in-ﬁlling scheme,\nwhere spans of text are replaced with a sin-\ngle mask token. BART is particularly ef-\nfective when ﬁne tuned for text generation\nbut also works well for comprehension tasks.\nIt matches the performance of RoBERTa on\nGLUE and SQuAD, and achieves new state-\nof-the-art results on a range of abstractive di-\nalogue, question answering, and summariza-\ntion tasks, with gains of up to 3.5 ROUGE.\nBART also provides a 1.1 BLEU increase over\na back-translation system for machine transla-\ntion, with only target language pretraining. We\nalso replicate other pretraining schemes within\nthe BART framework, to understand their ef-\nfect on end-task performance.1\n1 Introduction\nSelf-supervised methods have achieved remarkable\nsuccess in a wide range of NLP tasks (Mikolov et al.,\n2013; Peters et al., 2018; Devlin et al., 2019; Joshi\net al., 2019; Yang et al., 2019; Liu et al., 2019).\nThe most successful approaches have been variants of\nmasked language models, which are denoising autoen-\ncoders that are trained to reconstruct text where a ran-\ndom subset of the words has been masked out. Recent\nwork has shown gains by improving the distribution of\n1Code and pre-trained models for BART are avail-\nable at https://github.com/pytorch/fairseq\nandhttps://huggingface.co/transformersmasked tokens (Joshi et al., 2019), the order in which\nmasked tokens are predicted (Yang et al., 2019), and the\navailable context for replacing masked tokens (Dong\net al., 2019). However, these methods typically focus\non particular types of end tasks (e.g. span prediction,\ngeneration, etc.), limiting their applicability.\nIn this paper, we present BART, which pre-trains\na model combining Bidirectional and Auto-Regressive\nTransformers. BART is a denoising autoencoder built\nwith a sequence-to-sequence model that is applicable\nto a very wide range of end tasks. Pretraining has\ntwo stages (1) text is corrupted with an arbitrary nois-\ning function, and (2) a sequence-to-sequence model is\nlearned to reconstruct the original text. BART uses a\nstandard Tranformer-based neural machine translation\narchitecture which, despite its simplicity, can be seen as\ngeneralizing BERT (due to the bidirectional encoder),\nGPT (with the left-to-right decoder), and many other\nmore recent pretraining schemes (see Figure 1).\nA key advantage of this setup is the noising ﬂexibil-\nity; arbitrary transformations can be applied to the orig-\ninal text, including changing its length. We evaluate\na number of noising approaches, ﬁnding the best per-\nformance by both randomly shufﬂing the order of the\noriginal sentences and using a novel in-ﬁlling scheme,\nwhere arbitrary length spans of text (including zero\nlength) are replaced with a single mask token. This ap-\nproach generalizes the original word masking and next\nsentence prediction objectives in BERT by forcing the\nmodel to reason more about overall sentence length and\nmake longer range transformations to the input.\nBART is particularly effective when ﬁne tuned for\ntext generation but also works well for comprehen-\nsion tasks. It matches the performance of RoBERTa\n(Liu et al., 2019) with comparable training resources\non GLUE (Wang et al., 2018) and SQuAD (Rajpurkar\net al., 2016), and achieves new state-of-the-art results\non a range of abstractive dialogue, question answering,\nand summarization tasks. For example, it improves\nperformance by 3.5 ROUGE over previous work on\nXSum (Narayan et al., 2018).\nBART also opens up new ways of thinking about ﬁne\ntuning. We present a new scheme for machine transla-\ntion where a BART model is stacked above a few ad-\nditional transformer layers. These layers are trained\n\n7872\nBidirectional EncoderA  _  C  _  E B       D    (a) BERT: Random tokens are replaced with masks, and\nthe document is encoded bidirectionally. Missing tokens\nare predicted independently, so BERT cannot easily be\nused for generation.\nAutoregressive DecoderA  B  C  D  E<s> A  B  C  D  (b) GPT: Tokens are predicted auto-regressively, meaning\nGPT can be used for generation. However words can only\ncondition on leftward context, so it cannot learn bidirec-\ntional interactions.\nAutoregressive DecoderBidirectional EncoderA  B  C  D  EA  _  B  _  E         <s> A  B  C  D  \n(c) BART: Inputs to the encoder need not be aligned with decoder outputs, allowing arbitary noise transformations. Here, a\ndocument has been corrupted by replacing spans of text with a mask symbols. The corrupted document (left) is encoded with\na bidirectional model, and then the likelihood of the original document (right) is calculated with an autoregressive decoder.\nFor ﬁne-tuning, an uncorrupted document is input to both the encoder and decoder, and we use representations from the ﬁnal\nhidden state of the decoder.\nFigure 1: A schematic comparison of BART with BERT (Devlin et al., 2019) and GPT (Radford et al., 2018).\nto essentially translate the foreign language to noised\nEnglish, by propagation through BART, thereby us-\ning BART as a pre-trained target-side language model.\nThis approach improves performance over a strong\nback-translation MT baseline by 1.1 BLEU on the\nWMT Romanian-English benchmark.\nTo better understand these effects, we also report\nan ablation analysis that replicates other recently pro-\nposed training objectives. This study allows us to care-\nfully control for a number of factors, including data\nand optimization parameters, which have been shown\nto be as important for overall performance as the se-\nlection of training objectives (Liu et al., 2019). We ﬁnd\nthat BART exhibits the most consistently strong perfor-\nmance across the full range of tasks we consider.\n2 Model\nBART is a denoising autoencoder that maps a corrupted\ndocument to the original document it was derived from.\nIt is implemented as a sequence-to-sequence model\nwith a bidirectional encoder over corrupted text and a\nleft-to-right autoregressive decoder. For pre-training,\nwe optimize the negative log likelihood of the original\ndocument.\n2.1 Architecture\nBART uses the standard sequence-to-sequence Trans-\nformer architecture from (Vaswani et al., 2017), ex-\ncept, following GPT, that we modify ReLU activa-\ntion functions to GeLUs (Hendrycks & Gimpel, 2016)\nand initialise parameters from N(0;0:02). For ourbase model, we use 6 layers in the encoder and de-\ncoder, and for our large model we use 12 layers in\neach. The architecture is closely related to that used in\nBERT, with the following differences: (1) each layer of\nthe decoder additionally performs cross-attention over\nthe ﬁnal hidden layer of the encoder (as in the trans-\nformer sequence-to-sequence model); and (2) BERT\nuses an additional feed-forward network before word-\nprediction, which BART does not. In total, BART con-\ntains roughly 10% more parameters than the equiva-\nlently sized BERT model.\n2.2 Pre-training BART\nBART is trained by corrupting documents and then op-\ntimizing a reconstruction loss—the cross-entropy be-\ntween the decoder’s output and the original document.\nUnlike existing denoising autoencoders, which are tai-\nlored to speciﬁc noising schemes, BART allows us to\napply anytype of document corruption. In the extreme\ncase, where all information about the source is lost,\nBART is equivalent to a language model.\nWe experiment with several previously proposed and\nnovel transformations, but we believe there is a sig-\nniﬁcant potential for development of other new alter-\nnatives. The transformations we used are summarized\nbelow, and examples are shown in Figure 2.\nToken Masking Following BERT (Devlin et al.,\n2019), random tokens are sampled and replaced with\n[MASK] elements.\nToken Deletion Random tokens are deleted from the\ninput. In contrast to token masking, the model must\n\nSummarization To provide a comparison with the\nstate-of-the-art in summarization, we present results\non two summarization datasets, CNN/DailyMail and\nXSum, which have distinct properties (Table 4).\nSummaries in the CNN/DailyMail tend to resemble\nsource sentences. Extractive models do well here, and\neven the baseline of the ﬁrst-three source sentences is\nhighly competitive. Nevertheless, BART outperforms\nall existing work.\nIn contrast, XSum is highly abstractive, and extrac-\ntive models perform poorly. BART outperforms the\nbest previous work, based on RoBERTa, by roughly 3.5\npoints on all ROUGE metrics—representing a signiﬁ-\ncant advance in performance on this problem. Qualita-\ntively, sample quality is high (see x6).\nWe also conduct human evaluation (Table 5). An-\nnotators were asked to choose the better of two sum-\nmaries for a passage. One summary was from BART,\nand the other was either a human reference or publicly\navailable output from the B ERTSUMEXTABSmodel.\nAs with automated metrics, BART signiﬁcantly outper-\nforms prior work. However, it has not reach human\nperformance on this task.\nDialogue We evaluate dialogue response generation\non C ONVAI2 (Dinan et al., 2019), in which agents\nmust generate responses conditioned on both the pre-\nvious context and a textually-speciﬁed persona. BART\noutperforms previous work on two automated metrics.\nAbstractive QA We use the recently proposed ELI5\ndataset to test the model’s ability to generate long free-\nform answers. We ﬁnd BART outperforms the best pre-\nvious work by 1.2 ROUGE-L, but the dataset remains\na challenging, because answers are only weakly speci-\nﬁed by the question.','We present BART, a Denoising autoencoder for pretraining Sequence-to-Sequence models. BART is trained by corrupting text with an arbitrary noising function, and learning a model to reconstruct the original text. BART matches the performance of RoBERTa on GLUE and SQuAD. BART also provides a 1.1 BLEU increase over a back-Translation system for machine transla. BART is a denoising autoencoder built with a sequence-to-sequence model that is applicable to a very wide range of end tasks. BART uses a standard Tranformer-based neural machine translation architecture. BART is particularly effective when ﬁne tuned for text generation but also works well for comprehen- sion tasks. BART is a denoising autoencoder that maps a corrupted document to the original document it was derived from. It is implemented as a sequence-to-sequence Model with a bidirectional encoder over corrupted text and a left- to-right autoregressive decoder. BART exhibits the most consistently strong perfor- mance across the full range of tasks we consider. The transformations We used are summarized below, and examples are shown in Figure 2. We present results on two Summarization datasets, CNN/DailyMail and XSum, which have distinct properties. BART outperforms previous work on two automated metrics.',10469,1282,192,'2026-02-11 11:22:16',NULL);
/*!40000 ALTER TABLE `summaries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'alex','$2a$10$R1FO3tIzqvhBkEbi1yPuIe2AOxwVpXGGd8B7fMfdF6mPu04x31kHW');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-14 16:19:50
