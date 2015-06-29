//
//package xmlProcessor.Backups;
//
//
//
//
//import xmlProcessor.RG00.XTCxqueryRG00Op;
//import xmlProcessor.RG00.XTCxqueryResult;
//
// //package xtc.server.xmlSvc.procedure;
//
//import java.util.ArrayList;
//
////import xmlProcessor.RG00.XTCxqueryResult;
//import xmlProcessor.xtcServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import xtc.server.accessSvc.idxMgr.idxBuilders.SingleIdxDefinition;
//import xmlProcessor.xtcServer.utils.XTCsvrCfg;
//import xtc.server.util.monitor.Counter;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xmlProcessor.xtcServer.operators.XTCxqueryOperator;
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
//import xmlProcessor.ExTwigList.XTCxqueryExTwigListOp;
//import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.QTP.QueryTreePattern;
//import xmlProcessor.QTP.QTPNode.OpType;
////import xmlProcessor.RG00.XTCxqueryRG00Op;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.XTCxqueryRG01Op;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.XTCxqueryRG02Op;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.XTCxqueryRG03Op;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.XTCxqueryRG04Op;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.XTCxqueryRG05Op;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.XTCxqueryRG06Op;
//import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.TJFast.XTCxqueryTJFastOp;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.TJFastNoPipe.XTCxqueryTJFastNoPipeOp;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.TwigList.XTCxqueryTwigListOp;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.TwigStack.XTCxqueryTwigStackOp;
//import xmlProcessor.xtcServer.utils.QueryTuple;
//
///**
// * @author Kamyar
// *
// */
//public class Twig implements Procedure
//{
//
//	private static final String INFO = "Executes RG method";
//	private static final String[] PARAMETER = new String[]{"Method - Specifies method to be used for executing query: [TwigStack, TJFast, TwigList, RG, RG01]",
//		                                                   "DOCUMENT - name of the document"};
//	
//	private XTCxqueryContext context = null;
//	private XTClocator locator = null;
//	
//	private String TwigMethod;
//	private int planNo = 0 ;
//	
//	private long startTime = 0;
//	private long endTime = 0;
//	
//	private long matchCount = 0;
//	private long executionTime = 0;
//	private long readPages = 0;
//	private long readPagesAttempts = 0;
//	private long numberOfReadElements = 0;
//	private long IOTime = 0;
//	private long numberOfExecutionPlans = 0;
//	private long numberOfGroupedExecutionPlans = 0;
//	private String executionMode = "N/A";
//	//TODO: the inxNo should be set later by execution manager
//	private int idxNo =0;
//        ////added by me:
//        public static xmlProcessor.RG00.Twig Instance=new xmlProcessor.RG00.Twig();
//	////
//	public String getName()
//	{
//		return getClass().getSimpleName();
//	}
//
//	public String getInfo()
//	{
//		return INFO;
//	}
//	
//	public String[] getParameter()
//	{
//		return PARAMETER;
//	}
//
//	public void prepare(XTCxqueryContext context, String... params) throws DBException
//	{
//		if ((params == null) || ((params.length != 4) && (params.length != 3)))
//			throw new DBException(getClass(), "prepare", "Invalid parameters: %s", (params == null) ? null : params.toString());
//		
//		try {
//			planNo = Integer.parseInt(params[2]);
//		} catch (Exception e) {
//			throw new DBException(getClass(), "prepare", "Invalid plan number: %s", params[1].toString());
//		} 
//		
//		this.context = context;
//		//this.locator = context.getXmlMgr().getLocator(context.getTransaction(), params[1]);		
//		//this.context.setLocator(locator);
//		
//	      	this.TwigMethod = params[0].toUpperCase();
//                ///************Must change to take my index:
//		ArrayList<SingleIdxDefinition> idxes;
//	        //idxes = context.getMetaDataMgr().getIndexes(context.getTransaction(), params[1]);
//		///***********
//                if (params.length == 3)
//		{
//			idxNo = idxes.get(0).getIDasInt();	
//		}
//		else
//		{
//			idxNo = Integer.parseInt(params[3]); //idxes.get(Integer.parseInt(params[3])).getIDasInt();
//		}
//		
//		//this.idxNo = Integer.parseInt(params[3]);
////		if (!TwigMethod.equals("TWIGSTACK") && !TwigMethod.equals("TJFAST") && !TwigMethod.equals("TWIGLIST") && !TwigMethod.equals("RG"))
////		{
////			throw new DBException(getClass(), "prepare", "Invalid twig method name: %s", TwigMethod);			
////		}
//	}
//
//	public XTCxqueryResult execute() throws DBException
//	{
//		switch (planNo) {
//		case 101:
//			
//			return execPlan101();
//
//		case 102:
//			
//			return execPlan102();
//
//		case 103:
//			
//			return execPlan103();
//
//		case 104:
//			
//			return execPlan104();
//
//		case 105:
//			
//			return execPlan105();
//
//		case 106:
//			
//			return execPlan106();
//
//		case 107:
//			
//			return execPlan107();
//
//		case 108:
//			
//			return execPlan108();
//
//		case 109:
//			
//			return execPlan109();
//
//		case 110:
//			
//			return execPlan110();
//
//		case 111:
//			
//			return execPlan111();
//
//		case 112:
//			
//			return execPlan112();
//
//		case 113:
//			
//			return execPlan113();
//
//		case 114:
//			
//			return execPlan114();
//
//		case 115:
//			
//			return execPlan115();
//
//		case 201:
//			
//			return execPlan201();
//
//		case 202:
//			
//			return execPlan202();
//
//		case 203:
//			
//			return execPlan203();
//
//		case 204:
//			
//			return execPlan204();
//
//		case 205:
//			
//			return execPlan205();
//
//		case 206:
//			
//			return execPlan206();
//
//		case 207:
//			
//			return execPlan207();
//
//		case 208:
//			
//			return execPlan208();
//
//		case 301:
//			
//			return execPlan301();
//
//		case 302:
//			
//			return execPlan302();
//
//		case 303:
//			
//			return execPlan303();
//
//		case 304:
//			
//			return execPlan304();
//
//		case 305:
//			
//			return execPlan305();
//
//		case 306:
//			
//			return execPlan306();
//
//		case 307:
//			
//			return execPlan307();
//
//		case 308:
//			
//			return execPlan308();
//
//		case 309:
//			
//			return execPlan309();
//
//		case 310:
//			
//			return execPlan310();
//
//		case 311:
//			
//			return execPlan311();
//
//		case 312:
//			
//			return execPlan312();
//
//		case 313:
//			
//			return execPlan313();
//
//		case 314:
//			
//			return execPlan314();
//
//		case 315:
//			
//			return execPlan315();
//
//		case 401:
//			
//			return execPlan401();
//
//		case 402:
//			
//			return execPlan402();
//
//		case 403:
//			
//			return execPlan403();
//
//		case 404:
//			
//			return execPlan404();
//
//		case 405:
//			
//			return execPlan405();
//
//		case 406:
//			
//			return execPlan406();
//
//		case 407:
//			
//			return execPlan407();
//
//		case 408:
//			
//			return execPlan408();
//
//		case 409:
//			
//			return execPlan409();
//
//		case 501:
//			
//			return execPlan501();
//
//		case 502:
//			
//			return execPlan502();
//
//		case 503:
//			
//			return execPlan503();
//
//		case 504:
//			
//			return execPlan504();
//
//		case 505:
//			
//			return execPlan505();
//
//		case 506:
//			
//			return execPlan506();
//
//		case 507:
//			
//			return execPlan507();
//
//		case 508:
//			
//			return execPlan508();
//
//		case 509:
//			
//			return execPlan509();
//
//		case 510:
//			
//			return execPlan510();
//
//		case 600:
//			
//			return execPlan600();
//
//		case 601:
//			
//			return execPlan601();
//
//		default:
//			return new XTCxqueryResult("No plan executed");
//			
//		}
//	}
//
//	
//	public XTCxqueryResult execPlan101() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//                
//                out.append("//G[/H]/I/J/K\n");
//		QTPNode root = new QTPNode("G", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode h = new QTPNode("H", root, XTCsvrCfg.AXIS_CHILD);
//		QTPNode i = new QTPNode("I", root, XTCsvrCfg.AXIS_CHILD);
//		QTPNode j = new QTPNode("J", i, XTCsvrCfg.AXIS_CHILD);
//                QTPNode k = new QTPNode("K", j, XTCsvrCfg.AXIS_CHILD);                
//                
//		///commented by ouldouz:
////		out.append("//inproceedings//title[.//i]//sub\n");
////		QTPNode root = new QTPNode("inproceedings", null, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode title = new QTPNode("title", root, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode i = new QTPNode("i", title, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode sup = new QTPNode("sup", title, XTCsvrCfg.AXIS_DESCENDANT);
//		///
//                //Ouldouz: first it builds a tree,then it builds a qtp pbj from that tree		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan102() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//article//title[NOT(.//sub)]\n");
//		QTPNode root = new QTPNode("article", null, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QTPNode title = new QTPNode("title", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode sub = new QTPNode("sub", title, XTCsvrCfg.AXIS_DESCENDANT, true);
//		//QTPNode sup = new QTPNode("sup", title, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan103() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//dblp/inproceedings[title]/author\n");
//		QTPNode root = new QTPNode("dblp", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode inproceedings = new QTPNode("inproceedings", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode title = new QTPNode("title", inproceedings, XTCsvrCfg.AXIS_PARENT);
//		QTPNode author = new QTPNode("author", inproceedings, XTCsvrCfg.AXIS_PARENT);
//		
//				
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan104() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
////		out.append("//dblp/article[author][//title]//year\n");
////		QTPNode root = new QTPNode("dblp", null, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode article = new QTPNode("article", root, XTCsvrCfg.AXIS_PARENT);
////		QTPNode author = new QTPNode("author", article, XTCsvrCfg.AXIS_PARENT);
////		QTPNode title = new QTPNode("title", article, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode year = new QTPNode("year", article, XTCsvrCfg.AXIS_DESCENDANT);
//		out.append("//article//title\n");
//		QTPNode root = new QTPNode("article", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode title = new QTPNode("title", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//
//	public XTCxqueryResult execPlan105() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//inproceedings[author][.//title]//booktitle\n");
//		QTPNode root = new QTPNode("inproceedings", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode author = new QTPNode("author", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode title = new QTPNode("title", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode booktitle = new QTPNode("booktitle", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//				
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan106() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//dblp/inproceedings[.//cite/label][title]//author\n");
//		QTPNode root = new QTPNode("dblp", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode inproceedings = new QTPNode("inproceedings", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode cite = new QTPNode("cite", inproceedings, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode label = new QTPNode("label", cite, XTCsvrCfg.AXIS_PARENT);
//		QTPNode title = new QTPNode("title", inproceedings, XTCsvrCfg.AXIS_PARENT);		
//		QTPNode author = new QTPNode("author", inproceedings, XTCsvrCfg.AXIS_DESCENDANT);
//		
//				
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan107() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//dblp/article[author][.//title][.//url][.//ee]//year\n");
//		QTPNode root = new QTPNode("dblp", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode article = new QTPNode("article", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode author = new QTPNode("author", article, XTCsvrCfg.AXIS_PARENT);
//		QTPNode title = new QTPNode("title", article, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode url = new QTPNode("url", article, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode ee = new QTPNode("ee", article, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode year = new QTPNode("year", article, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan108() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//article[.//pages][.//volume][.//cite]//journal\n");		
//		QTPNode root = new QTPNode("article", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode mdate = new QTPNode("mdate", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode volume = new QTPNode("volume", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode cite = new QTPNode("cite", root, XTCsvrCfg.AXIS_DESCENDANT);		
//		QTPNode journal = new QTPNode("journal", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan109() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//article/title//sup//sub\n");
//		QTPNode root = new QTPNode("article", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode title = new QTPNode("title", root, XTCsvrCfg.AXIS_PARENT);		
//		QTPNode sup = new QTPNode("sup", title, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode sub = new QTPNode("sub", sup, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan110() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//dblp/inproceedings/booktitle\n");
//		QTPNode root = new QTPNode("dblp", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode title = new QTPNode("inproceedings", root, XTCsvrCfg.AXIS_PARENT);		
//		QTPNode sup = new QTPNode("booktitle", title, XTCsvrCfg.AXIS_PARENT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan111() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//inproceedings//title[(.//sub) OR (.//i)]\n");
//		QTPNode root = new QTPNode("inproceedings", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode title = new QTPNode("title", OpType.OR, root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode sub = new QTPNode("sub", title, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode i = new QTPNode("i", title, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan112() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//inproceedings//title[[NOT(.//sub)] OR [NOT(.//i)]]\n");
//		QTPNode root = new QTPNode("inproceedings", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode title = new QTPNode("title", OpType.OR, root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode sub = new QTPNode("sub", title, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode i = new QTPNode("i", title, XTCsvrCfg.AXIS_DESCENDANT, true);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan113() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//inproceedings[(.//pages) OR (.//crossref) OR (.//title//sub)]\n");
//		
//		QTPNode root = new QTPNode("inproceedings", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QTPNode pages = new QTPNode("pages", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode crossref = new QTPNode("crossref", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode title = new QTPNode("title", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode sub = new QTPNode("sub", title, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan114() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//article[(.//volume) OR (.//cite) OR (//journal)]\n");		
//		QTPNode root = new QTPNode("article", OpType.OR,null, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode mdate = new QTPNode("pages", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode volume = new QTPNode("volume", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode cite = new QTPNode("cite", root, XTCsvrCfg.AXIS_DESCENDANT);		
//		QTPNode journal = new QTPNode("journal", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan115() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		
//		out.append("//inproceedings[title[NOT(.//sup/i)][NOT(.//tt)]][.//cite/label]//booktitle\n");
//
//		QTPNode root = new QTPNode("inproceedings", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode a = new QTPNode("title", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode b = new QTPNode("sup", a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode c = new QTPNode("i", b, XTCsvrCfg.AXIS_PARENT);
//		QTPNode d = new QTPNode("tt", a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode e = new QTPNode("cite", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode f = new QTPNode("label", e, XTCsvrCfg.AXIS_PARENT);				
//		QTPNode g = new QTPNode("booktitle", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//				
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan201() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//S[.//VP/IN]//NP\n");
//		QTPNode root = new QTPNode("S", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode IN = new QTPNode("IN", VP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode NP = new QTPNode("NP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan202() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//S/VP/PP[IN]/NP/VBN\n");
//		QTPNode root = new QTPNode("S", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode PP = new QTPNode("PP", VP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode IN = new QTPNode("IN", PP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode NP = new QTPNode("NP", PP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode VBN = new QTPNode("VBN", NP, XTCsvrCfg.AXIS_PARENT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan203() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//VP[DT]//PRP_DOLLAR_\n");
//		QTPNode root = new QTPNode("VP", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode DT = new QTPNode("DT", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode PRP_DOLLAR_ = new QTPNode("PRP_DOLLAR_", root, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan204() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//S/VP//PP[.//NP/VBN]/IN\n");
//		QTPNode root = new QTPNode("S", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode PP = new QTPNode("PP", VP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode NP = new QTPNode("NP", PP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VBN = new QTPNode("VBN", NP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode IN = new QTPNode("IN", PP, XTCsvrCfg.AXIS_PARENT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan205() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//S/VP//PP[.//NN][.//NP[.//CD]/VBN]/IN\n");
//		QTPNode root = new QTPNode("S", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode PP = new QTPNode("PP", VP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode NN = new QTPNode("NN", PP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode NP = new QTPNode("NP", PP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode CD = new QTPNode("CD", NP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VBN = new QTPNode("VBN", NP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode IN = new QTPNode("IN", PP, XTCsvrCfg.AXIS_PARENT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan206() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//S[.//VP][.//NP]/VP/PP[IN]/NP/VBN\n");
//		QTPNode root = new QTPNode("S", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode NP = new QTPNode("NP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP2 = new QTPNode("VP", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode PP = new QTPNode("PP", VP2, XTCsvrCfg.AXIS_PARENT);
//		QTPNode IN = new QTPNode("IN", PP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode NP2 = new QTPNode("NP", PP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode VBN = new QTPNode("VBN", NP2, XTCsvrCfg.AXIS_PARENT);
//				
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan207() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//EMPTY[.//VP/PP//NNP][.//S[.//PP//JJ]//VBN]//PP/NP//_NONE_\n");
//		QTPNode root = new QTPNode("EMPTY", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode PP = new QTPNode("PP", VP, XTCsvrCfg.AXIS_PARENT);
//		QTPNode NNP = new QTPNode("NNP", PP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode S = new QTPNode("S", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode PP2 = new QTPNode("PP", S, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode JJ = new QTPNode("JJ", PP2, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VBN = new QTPNode("VBN", S, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode PP3 = new QTPNode("PP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode NP = new QTPNode("NP", PP3, XTCsvrCfg.AXIS_PARENT);
//		QTPNode _NONE_ = new QTPNode("_NONE_", NP, XTCsvrCfg.AXIS_DESCENDANT);
//
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan208() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//EMPTY[.//VP/PP//NNP][.//S[.//PP//JJ]//VBN]//PP/NP//_NONE_\n");
//		QTPNode root = new QTPNode("EMPTY", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode VP = new QTPNode("VP", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode PP = new QTPNode("PP", VP, XTCsvrCfg.AXIS_PARENT);
//		//QTPNode VP2 = new QTPNode("VP", PP, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode NNP = new QTPNode("NNP", PP, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode S = new QTPNode("S", root, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode PP2 = new QTPNode("PP", S, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode PP4 = new QTPNode("PP", PP2, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode JJ = new QTPNode("JJ", PP4, XTCsvrCfg.AXIS_PARENT);
////		QTPNode VBN = new QTPNode("VBN", S, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode PP3 = new QTPNode("PP", root, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode NP = new QTPNode("NP", PP3, XTCsvrCfg.AXIS_PARENT);
////		QTPNode NP2 = new QTPNode("NP", NP, XTCsvrCfg.AXIS_PARENT);
////		QTPNode NP3 = new QTPNode("NP", NP2, XTCsvrCfg.AXIS_PARENT);
////		QTPNode _NONE_ = new QTPNode("_NONE_", NP3, XTCsvrCfg.AXIS_DESCENDANT);
//
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan301() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("/site//open_auction[.//bidder/personref]//reserve\n");
//		QTPNode root = new QTPNode("site", null, XTCsvrCfg.AXIS_PARENT);
//		QTPNode open_auction = new QTPNode("open_auction", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode bidder = new QTPNode("bidder", open_auction, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode personref = new QTPNode("personref", bidder, XTCsvrCfg.AXIS_PARENT);
//		QTPNode reserve = new QTPNode("reserve", open_auction, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan302() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//people//person[.//address/zipcode]/profile/education\n");
//		QTPNode root = new QTPNode("people", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode person = new QTPNode("person", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode address = new QTPNode("address", person, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode zipcode = new QTPNode("zipcode", address, XTCsvrCfg.AXIS_PARENT);
//		QTPNode profile = new QTPNode("profile", person, XTCsvrCfg.AXIS_PARENT);
//		QTPNode education = new QTPNode("education", profile, XTCsvrCfg.AXIS_PARENT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan303() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item[location]/description//keyword\n");
//		QTPNode root = new QTPNode("item", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode location = new QTPNode("location", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode description = new QTPNode("description", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode keyword = new QTPNode("keyword", description, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan304() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item[location][.//mailbox/mail//emph]/description//keyword\n");
//		QTPNode root = new QTPNode("item", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode location = new QTPNode("location", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode mailbox = new QTPNode("mailbox", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode mail = new QTPNode("mail", mailbox, XTCsvrCfg.AXIS_PARENT);
//		QTPNode emph = new QTPNode("emph", mail, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode description = new QTPNode("description", root, XTCsvrCfg.AXIS_PARENT);
//		QTPNode keyword = new QTPNode("keyword", description, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan305() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//people//person[.//address/zipcode][name]/profile[.//age]/education\n");
//		QTPNode root = new QTPNode("people", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode person = new QTPNode("person", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode address = new QTPNode("address", person, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode zipcode = new QTPNode("zipcode", address, XTCsvrCfg.AXIS_PARENT);
//		QTPNode name = new QTPNode("name", person, XTCsvrCfg.AXIS_PARENT);
//		QTPNode profile = new QTPNode("profile", person, XTCsvrCfg.AXIS_PARENT);
//		QTPNode age = new QTPNode("age", profile, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode education = new QTPNode("education", profile, XTCsvrCfg.AXIS_PARENT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan306() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//open_auction[.//annotation[.//author]//parlist]//bidder//increase\n");
//		QTPNode root = new QTPNode("open_auction", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode annotation = new QTPNode("annotation", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode author = new QTPNode("author", annotation, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode parlist = new QTPNode("parlist", annotation, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode bidder = new QTPNode("bidder", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode increase = new QTPNode("increase", bidder, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan307() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item//description//text[NOT(.//emph)]\n");
//		QTPNode root = new QTPNode("item", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode description = new QTPNode("description", root, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode text = new QTPNode("text", description, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode emph = new QTPNode("emph", text, XTCsvrCfg.AXIS_DESCENDANT, true);
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan308() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item[location][quantity][//keyword]/name\n");
//		QTPNode item = new QTPNode("item", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode location = new QTPNode("location", item, XTCsvrCfg.AXIS_PARENT);
//		QTPNode quantity = new QTPNode("quantity", item, XTCsvrCfg.AXIS_PARENT);
//		QTPNode keyword = new QTPNode("keyword", item, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode name = new QTPNode("name", item, XTCsvrCfg.AXIS_PARENT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(item);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan309() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//open_auctions//annotation//emph//keyowrd\n");
//		QTPNode open_auctions = new QTPNode("open_auctions", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode annotaion = new QTPNode("annotation", open_auctions, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode emph = new QTPNode("emph", annotaion, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode keyword = new QTPNode("keyword", emph, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(open_auctions);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan310() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//asia/item//parlist/listitem\n");
//		QTPNode asia = new QTPNode("asia", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode item = new QTPNode("item", asia, XTCsvrCfg.AXIS_PARENT);
//		QTPNode parlist = new QTPNode("parlist", item, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode listitem = new QTPNode("listitem", parlist, XTCsvrCfg.AXIS_PARENT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(asia);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan311() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item//description[(.//text//bold) OR (.//parlist//emph)]\n");
//		QTPNode a = new QTPNode("item", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("description", OpType.OR, a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b1 = new QTPNode("text", OpType.AND, b, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode e = new QTPNode("bold", b1, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode f = new QTPNode("parlist", b, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode f1 = new QTPNode("emph", f, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan312() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item[(.//location) OR (.//quantity) OR (//parlist//keyword)]\n");
//		QTPNode a = new QTPNode("item", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("location", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b1 = new QTPNode("quantity", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("parlist", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode d = new QTPNode("keyword", c, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode e = new QTPNode("bold", b1, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode f = new QTPNode("parlist", b, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode f1 = new QTPNode("emph", f, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan313() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item[[NOT(.//keyword)] OR [NOT(.//location)] OR [(.//shipping)]]\n");
//		QTPNode a = new QTPNode("item", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("keyword", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode b1 = new QTPNode("location", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode c = new QTPNode("shipping", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode d = new QTPNode("keyword", c, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode e = new QTPNode("bold", b1, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode f = new QTPNode("parlist", b, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode f1 = new QTPNode("emph", f, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan314() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//item[[(.//shipping)][NOT(.//description[NOT(.//keyword)]]]\n");
//		QTPNode a = new QTPNode("item", OpType.AND, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("shipping", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("description", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode b1 = new QTPNode("keyword", OpType.AND, b, XTCsvrCfg.AXIS_DESCENDANT, true);    	
////		QTPNode d = new QTPNode("keyword", c, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode e = new QTPNode("bold", b1, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode f = new QTPNode("parlist", b, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode f1 = new QTPNode("emph", f, XTCsvrCfg.AXIS_DESCENDANT);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan315() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("/site/regions//item/location\n");
//		QTPNode a = new QTPNode("site", null, XTCsvrCfg.AXIS_PARENT);
//		QTPNode b = new QTPNode("regions", a, XTCsvrCfg.AXIS_PARENT);
//		QTPNode c = new QTPNode("item", b, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode d = new QTPNode("location", c, XTCsvrCfg.AXIS_PARENT);
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan401() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//source[//author[/initial]/lastName]//title\n");
//		QTPNode source = new QTPNode("source", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode author = new QTPNode("author", source, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode initial = new QTPNode("initial", author, XTCsvrCfg.AXIS_PARENT);
//		QTPNode lastName = new QTPNode("lastName", author, XTCsvrCfg.AXIS_PARENT);
//		QTPNode title = new QTPNode("title", source, XTCsvrCfg.AXIS_DESCENDANT, true);
//		
//		
//		QueryTreePattern QTP = new QueryTreePattern(source);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan402() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//descriptions[//para]//astroObject/name\n");
//		QTPNode descriptions = new QTPNode("descriptions", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode para = new QTPNode("para", descriptions, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode astroObject = new QTPNode("astroObject", descriptions, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode name = new QTPNode("name", astroObject, XTCsvrCfg.AXIS_PARENT);
//				
//		
//		QueryTreePattern QTP = new QueryTreePattern(descriptions);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan403() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//revisions[//year][//para]//creator\n");
//		QTPNode revisions = new QTPNode("revisions", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode year = new QTPNode("year", OpType.AND, revisions, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode para = new QTPNode("para", OpType.AND, revisions, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode creator = new QTPNode("creator", OpType.AND, revisions, XTCsvrCfg.AXIS_DESCENDANT);
//				
//		
//		QueryTreePattern QTP = new QueryTreePattern(revisions);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan404() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//tableHead[./tableLinks/tableLink/title]//fields/field[definition]/name\n");
//		QTPNode tableHead = new QTPNode("tableHead", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode tableLinks = new QTPNode("tableLinks", tableHead, XTCsvrCfg.AXIS_PARENT);
//		QTPNode tableLink = new QTPNode("tableLink", tableLinks, XTCsvrCfg.AXIS_PARENT);
//		QTPNode title = new QTPNode("title", tableLink, XTCsvrCfg.AXIS_PARENT);
//		QTPNode fields = new QTPNode("fields", tableHead, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode field = new QTPNode("field", fields, XTCsvrCfg.AXIS_PARENT);
//		QTPNode definition = new QTPNode("definition", field, XTCsvrCfg.AXIS_PARENT);
//		QTPNode name = new QTPNode("name", field, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(tableHead);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan405() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//textFile/description//footnote//para\n");
//		QTPNode textFile = new QTPNode("textFile", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode decription = new QTPNode("description", textFile, XTCsvrCfg.AXIS_PARENT);
//		QTPNode footnote = new QTPNode("footnote", decription, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode para = new QTPNode("para", footnote, XTCsvrCfg.AXIS_DESCENDANT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(textFile);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan406() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//history//creator[./lastName]\n");
//		QTPNode history = new QTPNode("history", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode creator = new QTPNode("creator", history, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode lastName = new QTPNode("lastName", creator, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(history);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan407() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//dataset//description[NOT(.//para)]\n");
//		QTPNode a = new QTPNode("dataset", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("description", a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("para", b, XTCsvrCfg.AXIS_DESCENDANT, true);
////		QTPNode d = new QTPNode("para", decription, XTCsvrCfg.AXIS_DESCENDANT, true);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan408() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//dataset[(.//para) OR (.//heading)]\n");
//		QTPNode a = new QTPNode("dataset", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("para", a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("heading", a, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode d = new QTPNode("definition", a, XTCsvrCfg.AXIS_DESCENDANT, true);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	
//	public XTCxqueryResult execPlan409() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//definition[(.//footnote) OR (.//para)]\n");
//		QTPNode a = new QTPNode("definition", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("footnote", a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("para", a, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode d = new QTPNode("definition", a, XTCsvrCfg.AXIS_DESCENDANT, true);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan501() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Entry//PIR[prim_id][sec_id]\n");
//		QTPNode Entry = new QTPNode("Entry", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode PIR = new QTPNode("PIR", Entry, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode prim = new QTPNode("prim_id", PIR, XTCsvrCfg.AXIS_PARENT);
//		QTPNode sec = new QTPNode("sec_id", PIR, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Entry);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan502() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Entry/Features[/DISULFID/Descr][/CHAIN/Descr]\n");
//		QTPNode Entry = new QTPNode("Entry", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode Features = new QTPNode("Features", Entry, XTCsvrCfg.AXIS_PARENT);
//		QTPNode DISULFID = new QTPNode("DISULFID", Features, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Descr = new QTPNode("Descr", DISULFID, XTCsvrCfg.AXIS_PARENT);
//		QTPNode CHAIN = new QTPNode("CHAIN", Features, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Descr2 = new QTPNode("Descr", CHAIN, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Entry);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan503() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Ref[Author][Commnet][DB]\n");
//		QTPNode Ref = new QTPNode("Ref", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode Author = new QTPNode("Author", Ref, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Commnet = new QTPNode("Commnet", Ref, XTCsvrCfg.AXIS_PARENT);
//		QTPNode DB = new QTPNode("DB", Ref, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Ref);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//	
//	public XTCxqueryResult execPlan504() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Feautures/Domain[/Descr]\n");
//		QTPNode Feautures = new QTPNode("Feautures", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode Domain = new QTPNode("Domain", Feautures, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Descr = new QTPNode("Descr", Domain, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Feautures);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	
//	public XTCxqueryResult execPlan505() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Features/Domain[/Descr]\n");
//		QTPNode Feautures = new QTPNode("Features", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode Domain = new QTPNode("DOMAIN", Feautures, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Descr = new QTPNode("Descr", Domain, XTCsvrCfg.AXIS_PARENT,  true);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Feautures);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan506() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Entry/Features[/DISULFID[/from][/to]/Descr][/CHAIN[/from][/to]/Descr]\n");
//		QTPNode Entry = new QTPNode("Entry", null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode Features = new QTPNode("Features", Entry, XTCsvrCfg.AXIS_PARENT);
//		QTPNode DISULFID = new QTPNode("DISULFID", Features, XTCsvrCfg.AXIS_PARENT);
//		QTPNode f1 = new QTPNode("from", DISULFID, XTCsvrCfg.AXIS_PARENT);
//		QTPNode t1 = new QTPNode("to", DISULFID, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Descr = new QTPNode("Descr", DISULFID, XTCsvrCfg.AXIS_PARENT);
//		QTPNode CHAIN = new QTPNode("CHAIN", Features, XTCsvrCfg.AXIS_PARENT);
//		QTPNode f2 = new QTPNode("from", CHAIN, XTCsvrCfg.AXIS_PARENT);
//		QTPNode t2 = new QTPNode("to", CHAIN, XTCsvrCfg.AXIS_PARENT);
//		QTPNode Descr2 = new QTPNode("Descr", CHAIN, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Entry);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan507() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Entry/Features[/DISULFID[/from][/to]/Descr][/CHAIN[/from][/to]/Descr]\n");
//		QTPNode Entry = new QTPNode("Entry", null, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode Features = new QTPNode("Features", Entry, XTCsvrCfg.AXIS_PARENT);
////		QTPNode DISULFID = new QTPNode("DISULFID", Features, XTCsvrCfg.AXIS_PARENT);
////		QTPNode Descr = new QTPNode("Descr", DISULFID, XTCsvrCfg.AXIS_PARENT);
////		QTPNode f1 = new QTPNode("from", DISULFID, XTCsvrCfg.AXIS_PARENT);
////		QTPNode t1 = new QTPNode("to", DISULFID, XTCsvrCfg.AXIS_PARENT);
////		QTPNode CHAIN = new QTPNode("CHAIN", Features, XTCsvrCfg.AXIS_PARENT);
////		QTPNode Descr2 = new QTPNode("Descr", CHAIN, XTCsvrCfg.AXIS_PARENT);
////		QTPNode f2 = new QTPNode("from", CHAIN, XTCsvrCfg.AXIS_PARENT);
////		QTPNode t2 = new QTPNode("to", CHAIN, XTCsvrCfg.AXIS_PARENT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(Entry);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan508() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Entry[mtype][NOT(.//Mod)][NOT(.//Descr)]\n");
//		QTPNode a = new QTPNode("Entry", OpType.AND, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("mtype", a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("Mod", a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		QTPNode dN = new QTPNode("Descr", a, XTCsvrCfg.AXIS_DESCENDANT, true);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan509() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//Entry[(.//Ref/Author) OR (.//Kwyword)]\n");
//		QTPNode a = new QTPNode("Entry", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("Ref", a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("Author", b, XTCsvrCfg.AXIS_PARENT);
//		QTPNode dN = new QTPNode("Keyword", a, XTCsvrCfg.AXIS_DESCENDANT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan510() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("????????????//Entry[(.//Ref/Author) OR (.//Kwyword)]\n");
//		QTPNode a = new QTPNode("Entry", OpType.AND, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("VARIANT", a, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("REPEAT", b, XTCsvrCfg.AXIS_PARENT);
////		QTPNode dN = new QTPNode("Keyword", a, XTCsvrCfg.AXIS_DESCENDANT);
//
//		
//		QueryTreePattern QTP = new QueryTreePattern(a);
//		
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
////	public XTCxqueryResult execPlan600() throws DBException
////	{
////		startTime = System.currentTimeMillis();
////		
////		StringBuilder out = new StringBuilder();
////		out.append("//A//C\n");
////		QTPNode a = new QTPNode("a", OpType.OR, null, XTCsvrCfg.AXIS_DESCENDANT);
////		QTPNode b = new QTPNode("b", OpType.OR, a, XTCsvrCfg.AXIS_DESCENDANT);
////		//QTPNode Domain = new QTPNode("c", a, XTCsvrCfg.AXIS_DESCENDANT, "2 2");
////		
////		QTPNode c = new QTPNode("c", a, XTCsvrCfg.AXIS_DESCENDANT);
////		
////		QTPNode d = new QTPNode("d", b, XTCsvrCfg.AXIS_DESCENDANT);
////		
////		QTPNode e = new QTPNode("e", b, XTCsvrCfg.AXIS_DESCENDANT);
////
////		
////		QueryTreePattern QTP = new QueryTreePattern(a);
////		
////		this.executeQTP(QTP, out);
////		
////		return new XTCxqueryResult(out.toString());
////				
////	}
//
//	public XTCxqueryResult execPlan600() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//A//C\n");
//		QTPNode r = new QTPNode("r", OpType.AND, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode a = new QTPNode("a", OpType.AND, r, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode b = new QTPNode("b", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT, true);
//		//QTPNode m = new QTPNode("m", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
//		//QTPNode g = new QTPNode("g", OpType.AND, m, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode c = new QTPNode("c", OpType.AND, a, XTCsvrCfg.AXIS_DESCENDANT);
//		//QTPNode d = new QTPNode("d", OpType.AND, m, XTCsvrCfg.AXIS_DESCENDANT);
////		
////		QTPNode d = new QTPNode("d", b, XTCsvrCfg.AXIS_DESCENDANT);
////		
////		QTPNode e = new QTPNode("e", b, XTCsvrCfg.AXIS_DESCENDANT);
////
//		
//		QueryTreePattern QTP = new QueryTreePattern(r);
//		QTP.setExtractionPoint(r);
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	public XTCxqueryResult execPlan601() throws DBException
//	{
//		startTime = System.currentTimeMillis();
//		
//		StringBuilder out = new StringBuilder();
//		out.append("//A//C\n");
//		//QTPNode r = new QTPNode("r", OpType.AND, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode e = new QTPNode("e", OpType.AND, null, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode g = new QTPNode("g", OpType.AND, e, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode h = new QTPNode("h", OpType.AND, e, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode k = new QTPNode("k", OpType.AND, h, XTCsvrCfg.AXIS_DESCENDANT);
//		QTPNode l = new QTPNode("l", OpType.AND, h, XTCsvrCfg.AXIS_DESCENDANT);
////		
////		QTPNode d = new QTPNode("d", b, XTCsvrCfg.AXIS_DESCENDANT);
////		
////		QTPNode e = new QTPNode("e", b, XTCsvrCfg.AXIS_DESCENDANT);
////
//		
//		QueryTreePattern QTP = new QueryTreePattern(e);
//		QTP.setExtractionPoint(e);
//		this.executeQTP(QTP, out);
//		
//		return new XTCxqueryResult(out.toString());
//				
//	}
//
//	//executes the constructed QTP and append the output to the out parameter
//	//It does not return an output to prevent the time of appending even it maybe low 
//	private void executeQTP(QueryTreePattern QTP, StringBuilder out) throws DBException
//	{	
//		
//		long sReadPagesAttempts = Counter.readPagesAttempts;
//		long sp = Counter.readPages;
//		Object operator = null;
//		if (TwigMethod.equals("TWIGSTACK"))
//		{
//			operator = new XTCxqueryTwigStackOp(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("TJFAST"))
//		{
//			operator = new XTCxqueryTJFastOp(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("TJFASTNOPIPE"))
//		{
//			operator = new XTCxqueryTJFastNoPipeOp(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("TWIGLIST"))
//		{
//			operator = new XTCxqueryTwigListOp(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("EXTWIGLIST"))
//		{
//			operator = new XTCxqueryExTwigListOp(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("RG00"))
//		{
//			operator = new XTCxqueryRG00Op(context, QTP, idxNo, true);
//                        //takes a suitable opertator
//		}
//		else if (TwigMethod.equals("RG01"))
//		{
//			operator = new XTCxqueryRG01Op(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("RG02"))
//		{
//			operator = new XTCxqueryRG02Op(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("RG03"))
//		{
//			operator = new XTCxqueryRG03Op(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("RG04"))
//		{
//			operator = new XTCxqueryRG04Op(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("RG05"))
//		{
//			operator = new XTCxqueryRG05Op(context, QTP, idxNo);
//		}
//		else if (TwigMethod.equals("RG06"))
//		{
//			operator = new XTCxqueryRG06Op(context, QTP, idxNo);
//		}
//		else
//		{
//			throw new DBException("Twig method " + TwigMethod + "is not sopported");
//		}
//		
//                //casts any opertator to a general operator
//		XTCxqueryOperator plan = (XTCxqueryOperator)operator;
//		plan.open(); //opens it,then it's going to produce MPs one by one
//		this.matchCount = 0;
//
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////		
////		FileWriter outFile = null;
////		PrintWriter outF = null;
////		try 
////		{
////			outFile = new FileWriter("Res-" + TwigMethod + ".txt");
////			outF = new PrintWriter(outFile);
////		}
////		catch (IOException e)
////		{
////			e.printStackTrace();
////			throw new DBException("TestTwigs: Error in writing results.");
////		}
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////
//		QueryTuple last = null; 
//		for(QueryTuple tuple = plan.next() ; tuple != null; tuple = plan.next()) 
//		{
////			last = tuple;
////			if (matchCount < 10)
////			{
////				out.append(tuple.toString() + "\n\n");
////			}
//			
//			this.matchCount++;
//
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////
////			outF.println(matchCount + ": " + tuple.toString());			
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////
//		}
//		//out.append(last.toString() + "\n\n");
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////
////		outF.close();
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////
//
//		plan.close();
//		
//		endTime = System.currentTimeMillis();
//		this.executionTime = (endTime - startTime);
//		long ep = Counter.readPages;
//		long eReadPagesAttempts = Counter.readPagesAttempts;
//		this.readPagesAttempts = eReadPagesAttempts-sReadPagesAttempts;
//		this.readPages = ep-sp;
//		
//		if (!TwigMethod.equals("TJFASTNOPIPE"))
//		{
//			this.numberOfReadElements = ((QueryStatistics)operator).getNumberOfReadElements();
//			this.IOTime = ((QueryStatistics)operator).getIOTime();
//		}
//
//		if (TwigMethod.equals("RG") || TwigMethod.equals("RG01"))
//		{
//			this.numberOfExecutionPlans = ((XTCxqueryRGExtendedStatistics)operator).getNumberOfExecutionPlans();
//			this.numberOfGroupedExecutionPlans = ((XTCxqueryRGExtendedStatistics)operator).getNumberOfGroupedExecutionPlans();
//			this.executionMode = ((XTCxqueryRGExtendedStatistics)operator).getExecutionMode();
//		}
//		
//		out.append(this.matchCount + " elements in " + this.executionTime + " milliseconds.");
//		
//		System.out.println("Execution time for method " + TwigMethod + " for query " + planNo + ": " + this.executionTime );
//		
//		System.out.println("Number of matches: " + this.matchCount);
//		
//		System.out.println(this.readPages + " pages has been read");
//		
//		System.out.println(this.readPagesAttempts + " pages has been attempted to be read");
//		
//		System.out.println("Number of read elements: " + this.numberOfReadElements);
//		
//		System.out.println("I/O time: " + this.IOTime);		
//		
//		System.out.println("################################################");
//	}
//
//	public long getExecutionTime()
//	{
//		return executionTime;
//	}
//	
//	public long getReadPages()
//	{
//		return readPages;
//	}
//	
//	public long getReadPagesAttempts()
//	{
//		return readPagesAttempts;
//	}
//	
//	public long getNumberOfReadElements()
//	{
//		return numberOfReadElements;
//	}
//	
//	public long getIOTime()
//	{
//		return IOTime;
//	}
//	
//	public long getMatchCount()
//	{
//		return matchCount;
//	}
//	
//	public long getNumberOfPlans()
//	{
//		return numberOfExecutionPlans;
//	}
//	
//	public long getNumberOfGroupedPlans()
//	{
//		return numberOfGroupedExecutionPlans;
//	}
//
//	public String getExecutionMode()
//	{
//		return executionMode;
//	}
//}
