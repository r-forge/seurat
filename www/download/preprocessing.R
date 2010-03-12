#1. preprocessing Exon Array Data from HuEx-1_0-st-v2 chips using aroma.affymetrix
#http://groups.google.com/group/aroma-affymetrix/web/human-exon-array-analysis
###############################################################################

#setup
rm(list=ls())
#setwd("~/workspace/myExonChips/ExonChips/")
library(aroma.affymetrix)
verbose <- Arguments$getVerbose(-1, timestamp=TRUE)

#gettingAnnotation
#Note that the folder structure has to be /project/annotationData/<chipType>
#Affymetrix provides clusterings of the exon probesets that are meant 
#to roughly correspond to genes (see Affymetrix documentation).
#An advantage of these groupings of the exons is that each exon and
#probe is uniquely mapped to only one transcript cluster. For exon array analysis, 
#we would like a special CDF which maps transcript cluster IDs to exon IDs. 
#Affymetrix does not provide a cdf for these mappings, but Ken Simpson created such 
#cdfs based on the design-time annotation.
#The cdfs are avaialble at:
#http://groups.google.com/group/aroma-affymetrix/web/affymetrix-defined-transcript-clusters
chipType <- "HuEx-1_0-st-v2"
cdf <- AffymetrixCdfFile$byChipType(chipType, tags="coreR3,A20071112,EP")
print(cdf)

#Defining CEL set 
cs <- AffymetrixCelSet$byName("myExonChips", cdf=cdf)
print(cs)
# RMA backgroundCorrection
bc <- RmaBackgroundCorrection(cs, tag="coreR3")
csBC <- process(bc,verbose=verbose)
#Quantile normalization
qn <- QuantileNormalization(csBC, typesToUpdate="pm")
print(qn)
csN <- process(qn, verbose=verbose)

#To fit a summary of the entire transcript 
#(i.e. estimate the overall expression for the transcript), do: 
plmTr <- ExonRmaPlm(csN, mergeGroups=TRUE)
print(plmTr)
#To fit the PLM to all of the data Transcript level e.g. exons(probsets) belonging to a transcript
#cluster (gene) are merged
fit(plmTr, verbose=verbose)
#To extract the estimates (transcript or probeset) use either 
#extractMatrix() or extractDataFrame() on the ChipEffectSet that corresponds to the plm object:
cesTr <- getChipEffectSet(plmTr)
trFit <- extractDataFrame(cesTr, addNames=TRUE)

#build exon annotation 
#The Affymetrix NetAffx transcript cluster annotation file is available at:
#http://www.affymetrix.com/Auth/analysis/downloads/na30/wtexon/HuEx-1_0-st-v2.na30.hg19.transcript.csv.zip
netaffx <- read.csv("~/workspace/myExonChips/ExonChips/annotationData
/chipTypes/HuEx-1_0-st-v2/HuEx-1_0-st-v2.na30.hg19.transcript.csv",skip=20) 
matchanno <- match(trFit[,1],netaffx[,1])
annot <- netaffx[matchanno,]

#extract refseq gene IDs
geneIDs <- character()
for(i in 1:nrow(annot)){
	geneIDs[i] <- strsplit(strsplit(as.character(annot[i,"gene_assignment"]),c("///"))[[1]][1],c(" //"))[[1]][1]
}

#use biomaRt to build an annotation according to ref_seq gene ids
library(biomaRt)
ensembl = useMart("ensembl", dataset = "hsapiens_gene_ensembl")
filters = listFilters(ensembl)
attributes = listAttributes(ensembl)
anno <- getBM(attributes = c("refseq_dna","ensembl_gene_id","external_gene_id","chromosome_name","band",
				"transcript_start","transcript_end","go_biological_process_id",
				"go_biological_process_linkage_type","name_1006","definition_1006"), 
		filters="refseq_dna",values =geneIDs , mart = ensembl)
refseqmatch <- match(geneIDs,anno[,1])
anno <- anno[refseqmatch,]
trFit <- trFit[,-c(1:5)]
#standardization
trFit <- t(scale(t(trFit)))
trFit <- cbind(geneIds,trFit) 

#filter out genes located on HSCHR6  
chrfilter <- which(transanno[,4]=="GL000209.1"|transanno[,4]=="HSCHR6_MHC_APD"
		|transanno[,4]=="HSCHR6_MHC_COX"|transanno[,4]=="HSCHR6_MHC_DBB"
		|transanno[,4]=="HSCHR6_MHC_MANN"|transanno[,4]=="HSCHR6_MHC_QBL"
		|transanno[,4]=="HSCHR6_MHC_SSTO")
anno <- anno[-chrfilter,]
trFit <- trFit[-chrfilter,]

#filter out genes for which the annotation is incomplete
nas <- which(is.na(anno[,4])|is.na(anno[,6])|is.na(anno[,7]))
anno <- anno[-nas,]
trFit <- trFit[-nas,]
str(anno)
str(trFit)
colnames(anno)[4] <- "ChromosomeNumber"

#export gene expression file
write.table(trFit,file="transcriptFull.txt",sep="\t",row.names=F,col.names=T)

#export annotation file
write.table(anno,file="transannoFull.txt",sep="\t",row.names=F,col.names=T)


#2. Copy Number Analysis of Whole Genome 6.0 SNP-Chips using aroma.affymetrix
#http://groups.google.com/group/aroma-affymetrix/web/total-copy-number-analysis-6-0
#####################################################################################
#setup
rm(list=ls())
#setwd("~/workspace/mySNPchips/SNPChips")
library(aroma.affymetrix)
verbose <- Arguments$getVerbose(-8, timestamp=TRUE)

#Get the "full" CDF (chip description file )from Affymetrix
#and place it in the folder /annotationData/chipTypes/GenomeWideSNP_6/
#Within this folder you will also need the according UGP (Unit Genome 
#Position) and UFL (Unit Fragment-Length) files. Both available at:
#http://groups.google.com/group/aroma-affymetrix/web/genomewidesnp-6
#Note aroma.affymetrix assumes a certain folder structure 

#Locate the CDF:
cdf <- AffymetrixCdfFile$byChipType("GenomeWideSNP_6", tags="Full")
print(cdf)

#setup the raw CEL set 
cs <- AffymetrixCelSet$byName("mySNPchips", cdf=cdf)
print(cs)
#calibration for allelic crosstalk between allele probe pairs
acc <- AllelicCrosstalkCalibration(cs, model="CRMAv2")
print(acc)
csC <- process(acc, verbose=verbose)
print(csC)
#Normalization for nucleotide-position probe sequence effects 
bpn <- BasePositionNormalization(csC, target="zero")
print(bpn)
csN <- process(bpn, verbose=verbose)
print(csN)
#Probe summarization 
plm <- AvgCnPlm(csN, mergeStrands=TRUE, combineAlleles=TRUE)
print(plm)
if (length(findUnitsTodo(plm)) > 0) {
	units <- fitCnProbes(plm, verbose=verbose)
	str(units)
	units <- fit(plm, verbose=verbose)
	str(units)
}
ces <- getChipEffectSet(plm)
print(ces)
#Normalization for PCR fragment-length effects 
fln <- FragmentLengthNormalization(ces, target="zero")
print(fln)
cesN <- process(fln, verbose=verbose)
print(cesN)

#There are two common use cases for CopyNumber analysis; 
#either one do a paired analysis where each sample is
#coupled with a unique reference (e.g. tumor/normal)
#or a non-paired analysis where each sample use the same common
#reference.  When a common reference is used, it is often the 
#average of a pool of samples.
#Here we used public available HapMap data as reference set.
#This HapMap SNP 6.0 data set is available at: 
#ftp://ftp.ncbi.nlm.nih.gov/hapmap/raw_data/hapmap3_affy6.0/HOMOS.tgz
#We applied the same normalization procedure to the reference set

csRef <- AffymetrixCelSet$byName("HapMapSNPchips", cdf=cdf)
accRef <- AllelicCrosstalkCalibration(csRef, model="CRMAv2")
print(accRef)
csCRef <- process(accRef, verbose=verbose)
print(csCRef)
bpnRef <- BasePositionNormalization(csCRef, target="zero")
print(bpnRef)
csNRef <- process(bpnRef, verbose=verbose)
plmRef <- AvgCnPlm(csNRef, mergeStrands=TRUE, combineAlleles=TRUE)
print(plmRef)
if (length(findUnitsTodo(plmRef)) > 0) {
	unitsRef <- fitCnProbes(plmRef, verbose=verbose)
	str(unitsRef)
	unitsRef <- fit(plmRef, verbose=verbose)
	str(unitsRef)
}
cesRef <- getChipEffectSet(plmRef)
print(cesRef)
flnRef <- FragmentLengthNormalization(cesRef, target="zero")
print(flnRef)
cesNRef <- process(flnRef, verbose=verbose)
print(cesNRef)


# Calculation of raw copy numbers (optional)
# Hapmap average as reference
theta    <- extractMatrix(cesN)
thetaRef  <- extractMatrix(cesNRef)
rownames(theta) <- rownames(thetaRef) <- getUnitNames(cdf)
thetaRef   <- rowMedians(thetaRef, na.rm=TRUE)
# Calculate the relative CNs (optional)
rawCN <- 2*theta/thetaRef

#Identification of copy-number regions (CNRs)
ces1     <- extract(cesN)
cesRef  <- extract(cesNRef)
cesR   <- getAverageFile(cesRef, verbose=verbose)
#fit the glad model 
glad <- GladModel(cesC,ceR)
fit(glad, verbose=TRUE)
CNRs <- getRegions(glad)

#To get the nucleotide positions of the SNPs we used the
#Affymetrix NetAffx annotation file available at:
#http://www.affymetrix.com/estore/support/file_download.affx?onloadforward=/
#analysis/downloads/na30/genotyping/GenomeWideSNP_6.na30.annot.csv.zip
snpaffx <- read.csv("~/mySNPChips/SNPChips/annotationData/chipTypes
/GenomeWideSNP_6/GenomeWideSNP_6.na30.annot.csv/GenomeWideSNP_6.na30.annot.csv",skip=22)
snpaffx <- snpaffx[,c(1,2,3,4,7)]
#filter out SNP locted on mitochondrial DNA or SNPs for which no chromosome information is available
rmove <- which(snpaffx$Chromosome=="---"|snpaffx$Chromosome=="MT")
snpaffx <- snpaffx[-rmove,]
gc()

#read gene annotation file
tra <- read.table("~/workspace/myExonChips/ExonChips/transannoFull.txt",header=T,sep="\t")

#filter out snps not located on any gene
nsnps <- nrow(snpaffx)
status <- numeric(snsnps)
for (i in 1:nsnps){
	temp <- tra[which(as.character(transanno[,4])==as.character(snpaffx[i,"Chromosome"])),]
    	innerstatus <- logical(nrow(temp))
			for (j in 1:nrow(temp)){
				innerstatus[j] <- (as.numeric(snpaffx[i,"Physical.Position"])
						 > tra[j,6] & as.numeric(snpaffx[i,"Physical.Position"]) < tran[j,7])
				if(innerstatus[j]==TRUE)break
			}
			status[i] <- sum(innerstatus,na.rm=T) 	
		}

status<-which(as.logical(status))
snpaffx <- snpaffx[status,]

#order snps arcording to the chromosome and physical position 
chr <- as.character(snpaffx$Chromosome)
chr[which(chr=="X")]<-"23"
chr[which(chr=="Y")]<-"24"
chr <- as.numeric(chr)
bp <- snpaffx$Physical.Position
ordering<-order(chr,partial=bp)
snpaffx <- snpaffx[ordering,]

#assign gain and loss states to each snp and sample according to the copy number regions
#of the GladModel
nsnps <- nrow(snpaffx)
States <- matrix(rep(rep(0,nrow(snpaffx)),length(regions)),ncol=length(regions))
for (j in 1:length(regions)){
	reg <- CNRs[[j]][,1:4]
	reg <- reg[-which(reg[,1]==25),]
	reg[which(reg[,1]==23),1] <- "X"
	reg[which(reg[,1]==24),1] <- "Y"
	for (i in 1:nsnps){
		pos <- which(snpaffx$Chromosome[i]==reg[,1]&snpaffx$Physical.Position[i]
						>=reg[,2] & snpaffx$Physical.Position[i] <= reg[,3])
		States[i,j] <- reg[pos,"status"]
		cat("run:",i*100/nsnps,"%_State",States[i,j],"Patient",j,"\n")
	}
}

#assign the physical position of the chromosome center
cen1 <- 124300000
cen2 <-93300000
cen3 <-91700000
cen4 <-50700000
cen5 <-47700000
cen6 <-60500000
cen7 <-59100000
cen8 <-45200000
cen9 <-51800000
cen10 <-40300000
cen11 <-52900000
cen12 <-35400000
cen13 <-16000000
cen14 <-15600000
cen15 <-17000000
cen16 <-38200000
cen17 <-22200000
cen18 <-16100000
cen19 <-28500000
cen20 <-27100000
cen21 <-12300000
cen22 <-11800000
cenX <-59500000
cenY <-11300000

ChromosomeCen <- numeric()
for(i in 1:nsnps){
	if(snpaffx$Chromosome[i]=="1")ChromosomeCen[i]<-cen1
	if(snpaffx$Chromosome[i]=="2")ChromosomeCen[i]<-cen2
	if(snpaffx$Chromosome[i]=="3")ChromosomeCen[i]<-cen3
	if(snpaffx$Chromosome[i]=="4")ChromosomeCen[i]<-cen4
	if(snpaffx$Chromosome[i]=="5")ChromosomeCen[i]<-cen5
	if(snpaffx$Chromosome[i]=="6")ChromosomeCen[i]<-cen6
	if(snpaffx$Chromosome[i]=="7")ChromosomeCen[i]<-cen7
	if(snpaffx$Chromosome[i]=="8")ChromosomeCen[i]<-cen8
	if(snpaffx$Chromosome[i]=="9")ChromosomeCen[i]<-cen9
	if(snpaffx$Chromosome[i]=="10")ChromosomeCen[i]<-cen10
	if(snpaffx$Chromosome[i]=="11")ChromosomeCen[i]<-cen11
	if(snpaffx$Chromosome[i]=="12")ChromosomeCen[i]<-cen12
	if(snpaffx$Chromosome[i]=="13")ChromosomeCen[i]<-cen13
	if(snpaffx$Chromosome[i]=="14")ChromosomeCen[i]<-cen14
	if(snpaffx$Chromosome[i]=="15")ChromosomeCen[i]<-cen15
	if(snpaffx$Chromosome[i]=="16")ChromosomeCen[i]<-cen16
	if(snpaffx$Chromosome[i]=="17")ChromosomeCen[i]<-cen17
	if(snpaffx$Chromosome[i]=="18")ChromosomeCen[i]<-cen18
	if(snpaffx$Chromosome[i]=="19")ChromosomeCen[i]<-cen19
	if(snpaffx$Chromosome[i]=="20")ChromosomeCen[i]<-cen20
	if(snpaffx$Chromosome[i]=="21")ChromosomeCen[i]<-cen21
	if(snpaffx$Chromosome[i]=="22")ChromosomeCen[i]<-cen22
	if(snpaffx$Chromosome[i]=="X")ChromosomeCen[i]<-cenX
	if(snpaffx$Chromosome[i]=="Y")ChromosomeCen[i]<-cenY
}

#reorder States matrix according to the order of the samples in the gene expression data set

#order of the samples in the gene expression set
cnames <- c("07KM6172","07PB5909","07PB7916","08PB2263","07KM3290","08KM3745","08PB0785","08PB2279",
		"08KM2421","08KM2548","08KM3714","08PB0756","07KM8991","07PB7953","08KM0227",
		"08KM1077","08KM1685","08KM2095","08KM3729","08KM3781","08PB2035","08PB3477",
		"08PB3645")

#order of the samples in the snp data (States matrix) set
cnamessnp<-c("07KM3290","07KM6172","07KM8991","07PB5909","07PB7916","07PB7953","08KM0227","08KM1077",
		"08KM1685","08KM2095","08KM2421","08KM2548","08KM3714","08KM3729","08KM3745","08KM3781",
		"08PB0756","08PB0785","08PB2035","08PB2263","08PB2279","08PB3477","08PB3645")

#assign the correct colnames sample.States   
ma <- match(cnames,cnamessnp)
for (i in 1:length(cnames)){
	cnames[i] <- paste(cnames[i],".States",sep="")
}
cnames
States <- States[,ma]
colnames(States) <- cnames
finalsnp <- cbind(snpaffx,ChromosomeCen,States)
colnames(finalsnp)[3]<-"ChromosomeNumber"
colnames(finalsnp)[5]<-"Mapping"
#export snp file
write.table(snpfinal,file="snpFull.txt",sep="\t",row.names=F,col.names=T)



