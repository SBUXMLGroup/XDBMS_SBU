package xmlProcessor.QTP;

import java.util.ArrayList;
import java.util.UUID;
import xmlProcessor.DBServer.DBException;


/**
 * This class used to create Query Tree Pattern nodes (Twig nodes). NOTICE: when
 * a node is created its parent is set!!! and also this node will be added to
 * its parent as its child automatically !!!
 *
 * @author Kamyar
 *
 */
public class QTPNode implements QTPNodeConstraint {

    //Defines type of a node in a QueryTreePattern(logical operators and query nodes(element nodes))
    public static enum OpType 
    {

        AND, OR
    }
    private UUID uuid; //set to track cloned nodes and finding the counterpart node in original tree.
    private QTPNode parent = null;
    private String nPath; //represent the path of this node from root of QTP up to this node
    private int level = 0;
    private int axis; //represent kind of relation of this node with its parent query node
    private boolean notAxis; //represents if the relation with parent node is a  "not" relation or not
    private ArrayList<QTPNode> children = new ArrayList<QTPNode>();
    private String name;
    private QTPNode.OpType opType; //represent which logical operator should be applied on the node's children
    //TODO: Constraints are now checked very simple (only equal operation for String) in this version. It needs some enhancements.
    private String constraintValue;
    private boolean hasConstraint;
    //////////////////////////////
    //Constraints could be used in future, needed methods and constructors should be designed 
    //////////////////////////////
    private QTPNodeConstraint constraint; //any constraint on the query node
    /////////////////////////////
    ////////////////////////////

    /**
     * Clone the node and all of its sub nodes, its parent is not set but the
     * level for cloned i set as the original object.
     */
    public QTPNode clone()
    {
        QTPNode cloned = null;
        try {
            cloned = new QTPNode(name, opType, null, axis, notAxis, constraintValue);
            cloned.uuid = this.uuid; //set to be able to trace cloned node to original node
            cloned.level = this.level;
            for (QTPNode cNode : children) {
                cloned.addChild(cNode.clone());
            }
        } catch (DBException e) {
            cloned = null;
            System.err.println("Exception occured while Clone() in class xmlProcessor.QTP.QTPNode");
        }
        return cloned;
    }

    /**
     * clones only the node itself
     *
     * @return
     */
    public QTPNode cloneOnlyNode() {
        QTPNode cloned = null;
        try {
            cloned = new QTPNode(name, opType, null, axis, notAxis, constraintValue);
            cloned.uuid = this.uuid; //set to be able to trace cloned node to original node
        } catch (DBException e) {
            cloned = null;
            System.err.println("Exception occured while CloneOnlyNode() in class xmlProcessor.QTP.QTPNode");
        }
        return cloned;
    }

    public QTPNode(String name, QTPNode.OpType opType, QTPNode parent, int axis) throws DBException {
        this(name, opType, parent, axis, false, null);
    }

    //Main Constructor
    public QTPNode(String name, QTPNode.OpType opType, QTPNode parent, int axis, boolean notAxis, String constraintValue) throws DBException {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.opType = opType;
        this.parent = parent;
        this.axis = axis;
        this.notAxis = notAxis;
        checkAxis();
        //Adds this new node to the nodes of the parent and set the level
        if (parent != null) {
            parent.addChild(this);
            //this.level = parent.level + 1; //HAS BEEN MOVED to addChild 
        } else {
            this.level = 0;
        }
        calcPath();
        this.constraint = null;

        if (constraintValue == null) {
            this.hasConstraint = false;
        } else {
            this.hasConstraint = true;
        }

        this.constraintValue = constraintValue;
    }

    public QTPNode(String name, QTPNode.OpType opType, QTPNode parent, int axis, boolean notAxis) throws DBException {
        this(name, opType, parent, axis, notAxis, null);
    }

    //logical AND is selected for applying on its children
    public QTPNode(String name, QTPNode parent, int axis) throws DBException {
        this(name, QTPNode.OpType.AND, parent, axis, false, null);
//    	this.name = name;
//    	this.opType = OpType.AND;
//		this.parent = parent;
//		this.axis = axis;
//		checkAxis();
//		//Adds this new node to the nodes of the parent and set level
//		if (parent != null)
//		{
//			parent.addChild(this);
//			this.level = parent.level + 1;
//		}
//		calcPath();
//		this.constraint = null;
    }

    public QTPNode(String name, QTPNode parent, int axis, boolean notAxis) throws DBException {
        this(name, QTPNode.OpType.AND, parent, axis, notAxis, null);
    }

    //This constructore used for simple constraint checking
    public QTPNode(String name, QTPNode parent, int axis, String constraintValue) throws DBException {
        this(name, QTPNode.OpType.AND, parent, axis, false, constraintValue);
    }

    private boolean checkAxis() throws DBException {
        if ((axis != QTPSettings.AXIS_DESCENDANT) && (axis != QTPSettings.AXIS_CHILD)) {
            throw new DBException("Not spported axis");
        }
        return true;
    }

    private void calcPath() {
        //Only child and descendant axis are supported 
        if (parent == null) {
            if (axis == QTPSettings.AXIS_DESCENDANT) {
                nPath = "//" + getName();
            } else {
                nPath = "/" + getName();
            }
        } else {
            if (axis == QTPSettings.AXIS_DESCENDANT) {
                nPath = parent.getPath() + "//" + getName();
            } else {
                nPath = parent.getPath() + "/" + getName();
            }
        }
    }

    /**
     * returned path is correct only for the first creation of object if it is
     * assigned to other parents this method will not returned new path
     *
     * @return
     */
    public String getPath() {
        return nPath;
    }

    /**
     * returned level is correct only for the first creation of object if it is
     * assigned to other parents this method will not returned new level
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public QTPNode.OpType getOpType() {
        return opType;
    }

    public QTPNode getParent() {
        return parent;
    }

    public int getAxis() {
        return axis;
    }

    public boolean hasNotAxis() {
        return notAxis;
    }

    public ArrayList<QTPNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return (children.size() > 0);
    }

    public boolean hasNOTChild() {
        for (QTPNode child : children) {
            if (child.hasNotAxis()) {
                return true;
            }
        }
        return false;
    }

    public boolean isBranching() {
        return children.size() > 1;
    }

    public void addChild(QTPNode child) throws DBException {
        if (child == null) {
            throw new DBException("Null child is not acceptable.");
        }
        if (children.indexOf(child) != -1) {
            throw new DBException("Child has been added before.");
        }
        this.children.add(child);
        child.parent = this;
        child.level = this.level + 1;
    }

    /**
     * Remove the specified child and set the parent of that child to null,
     * nothing else would be changed even level numbers
     *
     * @param child
     * @throws DBException
     */
    public void removeChild(QTPNode child) throws DBException {
        if (child == null) {
            throw new DBException("Null child is not acceptable.");
        }

        boolean removed = this.children.remove(child);
        child.parent = null;
        if (!removed) {
            throw new DBException("Child does not exists or can not be removed.");
        }

    }

    /**
     * Remove children and set the parent of that child to null, nothing else
     * would be changed even level numbers
     *
     * @param child
     * @throws DBException
     */
    public void removeChildren() throws DBException {
        QTPNode cNode;
        while (this.hasChildren()) {
            cNode = this.children.get(0);
            boolean removed = this.children.remove(cNode);
            cNode.parent = null;
            if (!removed) {
                throw new DBException("At least on of children does not exists or can not be removed.");
            }
        }

    }

    @Override
    public String toString() {
        if (notAxis) {
            return "!" + this.getName();
        }
        return this.getName();
    }
    /**implementing hashCode() and equals() are needed to use QTPNOde as key in 
     * hashmap
    */
    @Override
    public int hashCode() {
        int h;
        //h = level*31 + name.hashCode();
        h = name.hashCode();
        //h = level + name.hashCode() + 11;
        //h = 10;
        //h=level;
        return h;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof QTPNode) {
            QTPNode aQTPNode = (QTPNode) anObject;
            if (aQTPNode.uuid.equals(this.uuid)) {
                return true;
            }
        }
//		if (anObject instanceof QTPNode) 
//		{
//			QTPNode aQTPNode = (QTPNode)anObject;
//			if (aQTPNode.name.equals(this.name) && aQTPNode.level == this.level 
//					&& ((aQTPNode.parent == null && this.parent == null) || ( (aQTPNode.parent != null && this.parent != null)&&(aQTPNode.parent.equals(this.parent)))))
//			{
//				return true;
//			}
//		}
//		if (this.hashCode() == ((QTPNode)anObject).hashCode())
//			System.err.println("SSSSSSSSSSSSSSSSSSSS");
//
        return false;
    }

    @Override
    public boolean hasConstraint() {
        return hasConstraint;
    }

    @Override
    public boolean checkConstraint(String InputValue) {
//		if (this.constraintValue == null) 
//		{
//			return true;
//		}
        return this.constraintValue.compareTo(InputValue) == 0;
    }

    public UUID getUUID() {
        return uuid;
    }
}
