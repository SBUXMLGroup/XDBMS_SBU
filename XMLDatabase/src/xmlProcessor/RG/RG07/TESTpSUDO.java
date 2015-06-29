///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package xmlProcessor.RG.RG07;
//
//import indexManager.StructuralSummaryNode;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//import xmlProcessor.DBServer.DBException;
//import xmlProcessor.DBServer.utils.TwinObject;
//import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.QTP.QueryTreePattern;
//import xmlProcessor.RG.RG07.EvaluationTree.EvaluationTreeNode;
//import xmlProcessor.RG.RG07.EvaluationTree.EvaluationTreeNodeInput;
//import xmlProcessor.RG.RG07.EvaluationTree.SelfInput;
//
///**
// *
// * @author Micosoft
// */
//public class TESTpSUDO 
//{
//  private EvaluationTreeNodeInput getEvaluationTree(QueryTreePattern QTP, QTPNode root, RGGroupedCorrelatedPlansTuple correlatedPlan,String indexName) throws DBException, IOException
//    {    	
//    	EvaluationTreeNode evaluationTreeRootNode;
//    	
//    	if (!root.hasChildren())
//    	{
//    		InputWrapper inputWrapper;
//    		inputWrapper = new InputWrapper(QTP,root, correlatedPlan.getTargetCIDs(root),indexName, openedStreams, useSharedInput);    		
//    		evaluationTreeRootNode = new EvaluationTreeNode(qNodes, root, correlatedPlan);
//                SelfInput leaf=new SelfInput(inputWrapper);
//    		evaluationTreeRootNode.addChild(leaf, false);
//                
//                Thread tEN=new Thread(evaluationTreeRootNode);
//                evaluationTreeRootNode.setThread(tEN);
//                return evaluationTreeRootNode;
//    	}
//    	ArrayList<QTPNode> children;
//    	children = root.getChildren();
//    	if (children.size() == 1)
//    	{
//    		if (children.get(0).hasNotAxis()) //Has Not Operator
//    		{
//    			evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node, correlatedPlan);
//    			InputWrapper inputWrapper;
//    			inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getTargetCIDs(node),indexName, openedStreams, useSharedInput);
//    			SelfInput leaf=new SelfInput(inputWrapper);
//                        evaluationTreeRootNode.addChild(leaf, false);
////                      
//                        Thread tEN=new Thread(evaluationTreeRootNode);
//                        evaluationTreeRootNode.setThread(tEN);
//                                               
//    			EvaluationTreeNode child = (EvaluationTreeNode)getEvaluationTree(QTP, children.get(0), correlatedPlan,indexName);
//    			evaluationTreeRootNode.addChild(child, true);
//    		}
//                else //Does not have NOT axis
//    		{
//    			node = children.get(0);
//    			children = node.getChildren();
//    			while (children.size() == 1 && !children.get(0).hasNotAxis())//????? che halatie?
//    			{
//    				qNodes.add(new TwinObject<QTPNode, Set<StructuralSummaryNode>>(node, correlatedPlan.getTargetCIDs(node)));
//    				node = children.get(0);
//    				children = node.getChildren();
//    			}    		
//
//    			if (children.size() == 1 && children.get(0).hasNotAxis())
//    			{
//    				evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node, correlatedPlan);
//    				InputWrapper inputWrapper;
//    				inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getTargetCIDs(node),indexName, openedStreams, useSharedInput);
//    				SelfInput leaf=new SelfInput(inputWrapper);
//                                evaluationTreeRootNode.addChild(leaf, false);
////                                
//                                Thread tEN=new Thread(evaluationTreeRootNode);
//                                evaluationTreeRootNode.setThread(tEN);
//                                
//    				EvaluationTreeNode child = (EvaluationTreeNode)getEvaluationTree(QTP, children.get(0), correlatedPlan,indexName);
//    				evaluationTreeRootNode.addChild(child, true);
//    				
//    			}
//    			else if (children.size() > 0)
//    			{
//    				evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node, correlatedPlan);
//    				boolean hasOrdinaryChild = false;
//    				boolean hasNOTChild = false;
//    				boolean[] allOrdinaryFinished = new boolean[correlatedPlan.groupedPlansNo()];
//    				for (int i = 0; i < allOrdinaryFinished.length; i++)
//    				{
//    					allOrdinaryFinished[i] = true;
//    				}
//    				HashSet<StructuralSummaryNode> PCRs = new HashSet<StructuralSummaryNode>();
//    				for (int i = 0; i < node.getChildren().size(); i++)
//    				{
//    					EvaluationTreeNode child = (EvaluationTreeNode)getEvaluationTree(QTP, children.get(i), correlatedPlan,indexName);
//    					boolean notAxis = children.get(i).hasNotAxis();
//    					hasOrdinaryChild = hasOrdinaryChild || !notAxis;
//    					if (!notAxis)
//    					{
//    						for (int k = 0; k < correlatedPlan.groupedPlansNo(); k++)
//    						{
//    							allOrdinaryFinished[k] = allOrdinaryFinished[k] && (correlatedPlan.getPlan(k).getStructuralSummaryNode(children.get(i)) == null);	
//    						}
//    					}
//    					else
//    					{
//    						hasNOTChild = true;
//    					}
//    					evaluationTreeRootNode.addChild(child, notAxis);
//    				} 
//    				if (!hasOrdinaryChild)
//    				{
//    					InputWrapper inputWrapper;
//    					inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getTargetCIDs(node),indexName, openedStreams, useSharedInput);
//    					SelfInput leaf=new SelfInput(inputWrapper);
//                                        evaluationTreeRootNode.addChild(leaf, false); 
////                                        Thread tL=new Thread(leaf);
////                                        leaf.start(tL); 
////                                        Thread tInp=new Thread(inputWrapper);
//                                        //tInp.start();
//                                        
//    				}
//           //**********************************************************************************************************
//    				else if (node.getOpType() == QTPNode.OpType.OR)
//    				{
//    					if (hasNOTChild)
//    					{
//    						for (int i = 0; i < correlatedPlan.groupedPlansNo(); i++)
//    						{
//    							StructuralSummaryNode psNode = correlatedPlan.getPlan(i).getStructuralSummaryNode(node);
//    							if (psNode != null)
//    							{
//    								PCRs.add(psNode);
//    							}
//    						}
//        					InputWrapper inputWrapper;
//        					inputWrapper = new InputWrapper(QTP, node, PCRs,indexName, openedStreams, useSharedInput);
//        					SelfInput leaf=new SelfInput(inputWrapper);
//                                                evaluationTreeRootNode.addChild(leaf, false);   
////                                                Thread tL=new Thread(leaf);
////                                                leaf.start(tL);
////                                                Thread tInp=new Thread(inputWrapper);
//                                                //tInp.start();
//                                                
//    					}
//
//    				}
//
//    				//        		if (!hasOrdinaryChild || (allOrdinaryFinished && node.getOpType() == OpType.OR)) //all children are not connectors so selfInput is required 
//    				//        		{
//    				//    				InputWrapper inputWrapper;
//    				//    				inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getTargetCIDs(node), context, idxNo, openedStreams, useSharedInput);
//    				//    				evaluationTreeRootNode.setSelfInput(inputWrapper);   			
//    				//        		}
//    			}
//    			else //if (children.size() == 0)
//    			{
//    				if (node.hasNotAxis() && node.getParent().getChildren().size() == 1)
//    				{
//    					evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node.getParent(), correlatedPlan);
//    					InputWrapper inputWrapper;
//    					inputWrapper = new InputWrapper(QTP, node.getParent(), correlatedPlan.getTargetCIDs(node.getParent()),indexName, openedStreams, useSharedInput);
//    					SelfInput leaf=new SelfInput(inputWrapper);
//                                        evaluationTreeRootNode.addChild(leaf, false);
////                                        Thread tL=new Thread(leaf);
////                                        leaf.start(tL);
////                                        Thread tInp=new Thread(inputWrapper);
//                                        //tInp.start();
//                                        Thread tEN=new Thread(evaluationTreeRootNode);
//                                        evaluationTreeRootNode.setThread(tEN);
//                                        //tEN.start();
//    					EvaluationTreeNodeInput child = getEvaluationTree(QTP, node, correlatedPlan,indexName);
//    					evaluationTreeRootNode.addChild(child, true);
//    				}
//    				else
//    				{
//    					evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node, correlatedPlan);
//    					InputWrapper inputWrapper;
//    					inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getTargetCIDs(node),indexName, openedStreams, useSharedInput);
//    					SelfInput leaf=new SelfInput(inputWrapper);
//                                        evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
////                                        Thread tL=new Thread(leaf);
////                                        leaf.start(tL);
////                                        Thread tInp=new Thread(inputWrapper);
//                                        //tInp.start();
//                                        Thread tEN=new Thread(evaluationTreeRootNode);
//                                        evaluationTreeRootNode.setThread(tEN);
//                                        //tEN.start();
//                                        
//    				}
//    			}
//    		}
//    	}
//    	else
//    	{
//    		QTPNode node = root;
//    		evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node, correlatedPlan);    		
//    		boolean hasOrdinaryChild = false;
//    		boolean hasNOTChild = false;
//    		boolean[] allOrdinaryFinished = new boolean[correlatedPlan.groupedPlansNo()];
//    		for (int i = 0; i < allOrdinaryFinished.length; i++)
//			{
//				allOrdinaryFinished[i] = true;
//			}
//    		HashSet<StructuralSummaryNode> PCRs = new HashSet<StructuralSummaryNode>();
//    		for (int i = 0; i < node.getChildren().size(); i++)
//			{
//        		EvaluationTreeNodeInput child = getEvaluationTree(QTP, children.get(i), correlatedPlan,indexName);
//        		boolean notAxis = children.get(i).hasNotAxis();
//        		hasOrdinaryChild = hasOrdinaryChild || !notAxis;
//        		if (!notAxis)
//        		{
//        			for (int k = 0; k < correlatedPlan.groupedPlansNo(); k++)
//        			{
//        				allOrdinaryFinished[k] = allOrdinaryFinished[k] && (correlatedPlan.getPlan(k).getStructuralSummaryNode(children.get(i)) == null);	
//        			}
//        		}
//        		else
//        		{
//        			hasNOTChild = true;
//        		}
//        		evaluationTreeRootNode.addChild(child, notAxis);				
//			}    	
//    		if (!hasOrdinaryChild)
//    		{
//				InputWrapper inputWrapper;
//				inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getTargetCIDs(node),indexName, openedStreams, useSharedInput);
//				SelfInput leaf=new SelfInput(inputWrapper);
//                                evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false); 			
////	                        Thread tL=new Thread(leaf);
////                                leaf.start(tL);
////                                Thread tInp=new Thread(inputWrapper);
//                                //tInp.start();
//                                
//                                
//    		}
//    		else if (node.getOpType() == QTPNode.OpType.OR)
//    		{
////        		for (int i = 0; i < correlatedPlan.groupedPlansNo(); i++)
////    			{
////    				if (allOrdinaryFinished[i])
////    				{
////    					StructuralSummaryNode psNode = correlatedPlan.getPlan(i).getStructuralSummaryNode(node);
////    					if (psNode != null)
////    					{
////    						PCRs.add(psNode);
////    					}
////    				}
////    			}
//    			if (hasNOTChild)
//    			{
//    				for (int i = 0; i < correlatedPlan.groupedPlansNo(); i++)
//    				{
//
//    					StructuralSummaryNode psNode = correlatedPlan.getPlan(i).getStructuralSummaryNode(node);
//    					if (psNode != null)
//    					{
//    						PCRs.add(psNode);
//    					}
//
//    				}
//        			InputWrapper inputWrapper;
//    				inputWrapper = new InputWrapper(QTP, node, PCRs,indexName, openedStreams, useSharedInput);
//    				SelfInput leaf=new SelfInput(inputWrapper);
//                                evaluationTreeRootNode.addChild(leaf, false); 
////                                Thread tL=new Thread(leaf);
////                                leaf.start(tL);
////                                Thread tInp=new Thread(inputWrapper);
//                                //tInp.start();
//                                
//    			}
//    			
//    		}
//    	}
//    	Thread tE=new Thread(evaluationTreeRootNode);
//        evaluationTreeRootNode.setThread(tE);
////        evaluationTreeRootNode.start(tE);
//	return evaluationTreeRootNode;
//    }
// 
//    
//}
//
//if (!root.hasChildren)  
//     stream = groupedStream(GMP, root, Doc);
//     eNode = new EvalTreeNode(root, upperNodes,GMP);
//     eNode.setDirectInput(stream);
//     SelfInput leaf=new SelfInput(stream);
//     eNode.addChild(leaf);   
//     Thread tEN=new Thread(eNode);
//      evaluationTreeRootNode.setThread(tEN);
//      return evaluationTreeRootNode;
//
//else if (root.children.size() = 1) 
//         if (root.children[0].hasNOTAxis)
//            stream = groupedStream(GMP, root, Doc);
// 			eNode = new EvalTreeNode(node, upperNodes,GMP);
// 			eNode.setDirectInput(stream);
// 			SelfInput leaf=new SelfInput(stream);
//            evaluationTreeRootNode.addChild(leaf, false);                   
//            Thread tEN=new Thread(eNode);
//            eNode.setThread(tEN);
// 			cTree = getEvalTree(QTP, root.children[0],MP, Doc);
// 			eNode.addChild(cTree, true);
// 	  else
// 			node = root.children[0];
//			crn = node.children;
//			while (crn.size() = 1 & ! crn[0].hasNOTAxis)
// 					upperNodes.add(root, GMP(root).levels);
// 					node = node.children[0];
// 					crn = node.children;
// 			end while;
// 			if (crn.size() = 1 & crn[0].hasNOTAxis)
//					stream = groupedStream(GMP, node, Doc);
// 					eNode = new EvalTreeNode(node, upperNodes,GMP);
// 					eNode.setDirectInput(stream);
// 					SelfInput leaf=new SelfInput(inputWrapper);
//                    eNode.addChild(leaf, false);                            
//                    Thread tEN=new Thread(eNode);
//                    eNode.setThread(tEN);
// 					cTree = getEvalTree(QTP, crn[0], GMP,Doc);
//					eNode.addChild(cTree, true);
//		   else if (crn.size() > 1)
// 				eNode = new EvalTreeNode(node,upperNodes);   
//  				hasPOSChid = false;
// 				hasNOTChid = false;
// 				for each child in crn do
// 					cTree = getEvalTree(QTP, child,GMP,Doc);
//					notAxis = child.hasNOTAxis;
// 					if (notAxis)
// 						hasNOTAxis = true;
// 					else
// 						hasPOSChild = true;
// 					eNode.addChild(cTree, notAxis);
//				 end for;
// 	            if (!hasPOSChild && (hasNOTChild && OP(node) = OR))
// 				strm = groupedStream(GMP, node, Doc);
// 				eNode.setDirectInput(strm);
//	            endif;
// else //node is QTP leaf
// 		if (node.hasNOTAxis & node.parent.children.size() = 1)
//			 eNode = new EvalTreeNode(node.parent, upperNodes, GMP);
// 			 strm = groupedStream(GMP, node.parent,Doc);
// 			eNode.setDirectInput(strm);
// 			SelfInput leaf=new SelfInput(inputWrapper);
//            evaluationTreeRootNode.addChild(leaf, false);
//            Thread tEN=new Thread(evaluationTreeRootNode);
//             evaluationTreeRootNode.setThread(tEN);
//			cTree = getEvalTree(QTP, node, GMP, Doc);
//			 eNode.addChild(cTree, true);
// 		else
// 				eNode = new EvalTreeNode(node, upperNodes, GMP);
// 				strm = groupedStream(GMP, node, Doc);
// 				eNode.setDirectInput(strm);
// 				SelfInput leaf=new SelfInput(inputWrapper);
//                evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
//                Thread tEN=new Thread(evaluationTreeRootNode);
//                 evaluationTreeRootNode.setThread(tEN);
//      endif;
// endif;
// endif;
// else
// the same code as lines 33-47
// endif;
// return eNode;
