
/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
	private IAVLNode root;
	private IAVLNode max; 
	private IAVLNode min;
	
  public AVLTree () {
	  this.root=null;
  }
  
  
	
  public boolean empty() {
	if (root ==null)
		return true;
    return false; // to be replaced by student code
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
	IAVLNode x=this.root; // first node 
	//binary search
	if (root ==null) {
		return null;
	}
	while (x.getHeight()!=-1) {
		if (x.getKey()==k){
			return x.getValue();
		}
		else if (k>x.getKey()) {
			x=x.getRight();
		}
		else {
			x=x.getLeft();
		}
	}
	return null; //Key's not found
  }

  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
 
  public int insert(int k, String i) {
	   //if k already exists, do not insert.
	   if (search(k) != null)
		   return -1;
	   
	   //if tree is empty, create new root
	   IAVLNode temp = root;
	   if (temp == null) {
		   root = new AVLNode(k, i);
		   root.setRight(new AVLNode(-1, ""));
		   root.setLeft(new AVLNode(-1, ""));
		   root.setParent(null);
		   root.getRight().setHeight(-1);
		   root.getLeft().setHeight(-1);
		   root.getRight().setParent(root);
		   root.getLeft().setParent(root);
		   root.setHeight(0);
		   root.setSize(1);
		   min = max = root;
		   return 0;
	   }
	   
	 //search a leaf to insert new node under
	   while (temp.isRealNode()) {
		   temp.setSize(temp.getSize()+1);
		   if (temp.getKey() > k)
			   temp = temp.getLeft();
		   else
			   temp = temp.getRight();
	   }
	   
	   //insert new node under leaf and give it two virtual sons
	   IAVLNode newNode = new AVLNode(k,i);
	   if (temp == temp.getParent().getLeft())
		   temp.getParent().setLeft(newNode);
	   else
		   temp.getParent().setRight(newNode);
	   newNode.setRight(new AVLNode(-1, ""));
	   newNode.setLeft(new AVLNode(-1, ""));
	   newNode.setSize(1);
	   newNode.getRight().setHeight(-1);
	   newNode.getLeft().setHeight(-1);
	   newNode.getRight().setParent(newNode);
	   newNode.getLeft().setParent(newNode);
	   newNode.setHeight(0);
	   newNode.setParent(temp.getParent());
	   
	   //make corrections everywhere we need to

	   int correctionsCounter = balanceInsertion(newNode);
	   //update min and max
	   if (newNode.getKey() < min.getKey())
		   min = newNode;
	   if (newNode.getKey() > max.getKey())
		   max = newNode;
	   
	   return correctionsCounter;
  }
  
  
  /**
   * public int caseInsert(IAVLNode toCheck)
   *
   * return:
   * 0 : if the node is legal in AVL tree (is either (1,1),(1,2) or (2,1))
   * 1 : if it is case 1 (as shown in lecture)
   * 2 : if it is case 2 (as shown in lecture)
   * 3 : if it is case 3 (as shown in lecture) and the left symmetrical (fig. below) happens
   * 4 : if it is case 3 (as shown in lecture) and the right symmetrical (fig. below) happens
   * 5: if toCheck is root.
   * 
   * (two symmetrical options):
   * 
   *                z       .       z
   *              /   \     .     /   \
   *             /     \    .    /     \
   *         toCheck     y  .   y    toCheck     
   *          /   \         .         /   \
   *         /     \        .        /     \
   *        a       b       .       b       a
   *        
   *        
   * @pre: toCheck != null
   * @pre: toCheck is not virtual and has a parent with another son that isn't virtual.
   */
  
  public int caseInsert(IAVLNode toCheck) {
	   //check if toCheck is root (which means we fixed everything all the way to the top)
	   if (toCheck == root || toCheck == null) {
		   return 0;
	   }
	   
	   //check which of the two symmetrical options described above happens
	   boolean checkedIsLeft = true;
	   if (toCheck == toCheck.getParent().getRight())
		   checkedIsLeft = false;
	   
	   //assign y,a,b as shown in picture above
	   IAVLNode z = toCheck.getParent();
	   IAVLNode y;
	   IAVLNode a = toCheck.getLeft();
	   IAVLNode b = toCheck.getRight();
	   if (checkedIsLeft) {
		   y = z.getRight();
		   a = toCheck.getLeft();
		   b = toCheck.getRight();
	   }
	   else {
		   y = z.getLeft();
		   a = toCheck.getRight();
		   b = toCheck.getLeft();
	   }

	   //check the rank diff between each two nodes
	   int z_y = z.getHeight() - y.getHeight(); 
	   int z_toCheck = z.getHeight() - toCheck.getHeight();
	   int toCheck_a = toCheck.getHeight() - a.getHeight();
	   int toCheck_b = toCheck.getHeight() - b.getHeight();
	   
	   if (z_toCheck == 1)
		   return 0;
	   if (z_toCheck == 0 && z_y == 1)
		   return 1;
	   if (z_toCheck == 0 && z_y == 2 && toCheck_b == 2 && toCheck_a == 1)
		   return 2;
	   if (z_toCheck == 0 && z_y == 2 && toCheck_b == 1 && toCheck_a == 2) {
		   if (checkedIsLeft)
			   return 3;
		   else
			   return 4;
	   }
	   if (z_toCheck == 0 && z_y ==2 && toCheck_b == 1 && toCheck_a == 1)
		   return 5;
	   return -1;
  }
  
  /**
   * public int balanceInsertion(IAVLNode unbalanced)
   *
   * takes an inserted node or a node which has been promoted,
   * and uses CaseInsert to decide if you can fix the problem.
   * 
   * if possible, fixes the problem by promotions/rotations,
   * if not - passes the problem upwards.
   * 
   * returns the number of balancing operations.
   * 

   * @pre: unbalanced != null
   * @pre: unbalanced is the only illegal node whoch was caused by insertion/promotion
   */
  
  public int balanceInsertion(IAVLNode unbalanced) {
	   //make corrections everywhere we need to
	   int curCase;
	   //we'll use this counter to count balancing operations
	   int correctionsCounter = 0;
	   
	   while ((curCase = caseInsert(unbalanced)) != 0) {	
		   //CASE 1: pass the problem up
		   if (curCase == 1) {
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight() + 1);
			   correctionsCounter++;
		   }
		   //CASE 2: single rotation 
		   if (curCase == 2) {
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight() - 1);
			   rotate(unbalanced);
			   correctionsCounter += 2;
		   }
		   //CASE 3: double rotation 
		   if (curCase == 3) {
			   unbalanced.setHeight(unbalanced.getHeight()-1);
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight()-1);
			   rotate(unbalanced.getRight());
			   rotate(unbalanced.getParent());
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight()+1);
			   correctionsCounter += 5;
		   }
		   //CASE 3: double rotation (symmetrical) 
		   if (curCase == 4) {
			   unbalanced.setHeight(unbalanced.getHeight()-1);
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight()-1);
			   rotate(unbalanced.getLeft());
			   rotate(unbalanced.getParent());
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight()+1);
			   correctionsCounter += 5;
		   }
		   //CASE 5: single rotation 
		   if (curCase == 5) {
			   unbalanced.getParent().setHeight(unbalanced.getParent().getHeight() - 1);
			   rotate(unbalanced);
			   correctionsCounter+= 2;
		   }
		   
		   //move one up
		   unbalanced = unbalanced.getParent();
	   }

	   return correctionsCounter;
  }
  
  /**
   * public void rotate(IAVLNode son,IAVLNode parent )
   *
   * makes a rotation between son and parent
   * 
   * @pre: son is son of parent
   */

  
  public void rotate(IAVLNode son ) {
	   //set parent. if there is none then set to new root.
	   IAVLNode parent = son.getParent();
	   if (parent != root) {
		   son.setParent(parent.getParent());
		   if (parent == parent.getParent().getRight())
			   parent.getParent().setRight(son);
		   else
			   parent.getParent().setLeft(son);
	   }
	   else {
		   root = son;
		   root.setParent(null);
	   }
	   parent.setParent(son);
	   if (son == parent.getRight()) {
		   parent.setRight(son.getLeft());
		   parent.getRight().setParent(parent);
		   son.setLeft(parent);
	   }
	   else {
		   parent.setLeft(son.getRight());
		   parent.getLeft().setParent(parent);
		   son.setRight(parent);
	   }
	   
	   //update sizes
	   parent.setSize(parent.getRight().getSize() + parent.getLeft().getSize() + 1);
	   son.setSize(son.getRight().getSize() + son.getLeft().getSize() + 1);

  }

  /**
   * public IAVLNode search(int k)
   *
   * returns the node of an item with key k if it exists in the tree
   * otherwise, returns null
   */
   public IAVLNode searchNode (int k) {
	   IAVLNode x=this.root; // first node 
		//doing binary search
		while (x.getHeight()!=-1) {
			if (x.getKey()==k){
				return x;
			}
			else if (k>x.getKey()) {
				x=x.getRight();
			}
			else {
				x=x.getLeft();
			}
		}
		return null;
	   
   }
   
  
   /**
    * public int delete(int k)
    *
    * deletes an item with key k from the binary tree, if it is there;
    * the tree must remain valid (keep its invariants).
    * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
    * returns -1 if an item with key k was not found in the tree.
    * 
    *
    */
   
   
   
   
   public int delete(int k)
   {
	   IAVLNode node =searchNode(k);	// using search functions that returns the node itself
	   if (node==null) {
		   return -1; //Node to be deleted is not found
	   }
	   
	   int rebalance=0; //Counts the number of rebalance operations
	  

	    //Delete a root with only one child - left child
	   if (node.getKey()==root.getKey()&&node.getLeft().isRealNode() && !(node.getRight().isRealNode())) {
			   this.root=root.getLeft();
			   this.root.setHeight(0);
			   root.setParent(null);
			   max=this.root;
			   min=this.root;
			   this.root.setSize(1);

			   return rebalance;
		}
	   //delete a root with only one child= right child
	   if (node.getKey()==root.getKey()&&node.getRight().isRealNode() && !(node.getLeft().isRealNode())){
			    	this.root=root.getRight();
			    	this.root.setHeight(0);
				   root.setParent(null);
				   max=this.root;
				   min=this.root;
				   this.root.setSize(1);
				   return rebalance;
		}
	   //delete a root which is a leaf
		if (node.getKey()==root.getKey()&&!(node.getLeft().isRealNode()) && !(node.getRight().isRealNode())){ //root is a leaf
			  this.root=null;
			   return rebalance;
		 }
	   
		//If the node is min, update the new min
		if (node.getKey()==min.getKey()) {

			   min=successor(node);
		 }
		//if the node is max, update the new max
		 if (node.getKey()==max.getKey()) {
			   
			   max=predeccessor(node);
		 }
		
		//node has right and left child
	   if (node.getRight().isRealNode()&& node.getLeft().isRealNode()) { 
		   IAVLNode successor=successor(node);
		   if (successor.getKey()==this.max.getKey()) { //if successor is the min
			   this.max=node;
		   }
		   String valueSuccessor=successor.getValue();
		   String ValueNode=node.getValue();
		   int keySuccessor=successor.getKey();
		   int keyNode = node.getKey();
		   //Switch between the node and its' successor
		   successor.setKey(keyNode);
		   successor.setValue(ValueNode);
		   node.setKey(keySuccessor);
		   node.setValue(valueSuccessor);
		   node=successor;
		   
	   }
	   
	 //Updating size
	   IAVLNode temp2=node.getParent(); 
	   while (temp2!=null) {
		   temp2.setSize(temp2.getSize()-1);
		   temp2=temp2.getParent();
	   }
	   
	    //node is a leaf or unary node, and node is not the root of the tree
	   boolean leftOrRight=leftOrRight(node); //check if node is a right (=true) left (=false)
	   int []range = setRange(node, leftOrRight); //Operating setRange - explained above the function
	   

		   IAVLNode temp;
		   	   if (node.getLeft().isRealNode()) {    //The unary node has left son
		   		   temp=node.getLeft();

		   	   }
		   	   else { //The unary node has right son of the node is a leaf
		   		   temp=node.getRight();

		   	   }
		  

		   	   if (leftOrRight) {
		   		   node.getParent().setRight(temp);   //Delete node by connecting its parent to its son
		   		   temp.setParent(node.getParent());
		   	   }
		   	   else {
		   		   node.getParent().setLeft(temp);  //Delete node by connecting its parent to its son
		   		   temp.setParent(node.getParent());
		   		   
		   	   }

		   	   node=node.getParent();

			   while (node!=null){
				   
				   if (range[0]==1 && range [1]==1) {
					   //Problem Solved!
					   return rebalance;
				   }
				   //CASE1: range of node is 1-2
				   else if (range[0]==1 && range [1]==2) {
						   case1(node, leftOrRight, range);
						   rebalance+=1;
					   }
				   else { //range[0]==2 && range [1]==1
						   if (leftOrRight) { //node is a right child
							   //CASE 2: the ranges of the other child of the node's parent are 1-1
							   if (setRange(node.getLeft().getRight(), true)[0]==1 && setRange(node.getLeft().getRight(), true)[1]==1) {
							   		case2(node.getLeft(), leftOrRight(node));
							   		rebalance+=3;
							   		//Problem Solved!
							   		break;
							   }
							   //CASE 3: the ranges of the other child of the node's parent are 2-1
							   else if (setRange(node.getLeft().getRight(), true)[0]==2 && setRange(node.getLeft().getRight(), true)[1]==1) {
								   case3 (node.getLeft(), leftOrRight(node), range);
								   rebalance+=3;
								   node=node.getParent();

							   }
							   //CASE 4:the ranges of the other child of the node's parent are 1-2
							   else {
								   case4( node.getLeft().getRight(), leftOrRight, range);
								   rebalance+=6;
								   node=node.getParent();

							   }
						   }
						   else { //node is a left child
							   //CASE 2: the ranges of the other child of the node's parent are 1-1
							   if (setRange(node.getRight().getLeft(), false)[0]==1 && setRange(node.getRight().getLeft(), false)[1]==1) {
								   case2(node.getRight(), leftOrRight(node));
								   rebalance+=3;
								   break; //problem solved
							   }
							   //CASE 3: the ranges of the other child of the node's parent are 2-1
							   else if (setRange(node.getRight().getLeft(), false)[0]==2 && setRange(node.getRight().getLeft(), false)[1]==1) {
								   case3 (node.getRight(), leftOrRight(node),  range);
								   rebalance+=3;
								   node=node.getParent();

							   }
							   //CASE 4: the ranges of the other child of the node's parent are 1-2
							   else {
								   case4(node.getRight().getLeft(), leftOrRight, range);
								   rebalance+=6;
								   node=node.getParent();

							   }
						   
						   }
				   	}
			if (node.getParent()!=null) {
					leftOrRight=leftOrRight(node);
			}
			node=node.getParent();
		   }
		   
		return rebalance;
   }
   
   //case1 as shown in class
   private void case1 (IAVLNode node, boolean leftOrRight, int [] range) {

	   	range[0]=setRange(node, leftOrRight)[0];
	   	range[1]=setRange(node, leftOrRight)[1];
		node.setHeight(node.getHeight()-1);
   }
   
   //case2 as shown in class
   private void case2 (IAVLNode node, boolean leftOrRight) {

		node.getParent().setHeight(node.getParent().getHeight()-1);

	   	rotate(node);
		node.setHeight(node.getHeight()+1);

  }
   
   //case3 as shown in class
   private void case3 (IAVLNode node,boolean leftOrRight, int[] range) {
	   range[0]=setRange(node.getParent(), leftOrRight )[0];
	   range[1]=setRange(node.getParent(), leftOrRight )[1];
		node.getParent().setHeight(node.getParent().getHeight()-2);
	   rotate(node);
	   


 }
  
   //case4 as shown in class
   private void case4 (IAVLNode node, boolean leftOrRight, int[] range) {

	   IAVLNode temp1=node.getParent();
	   IAVLNode temp2=node.getParent().getParent();
	   range[0]=setRange(temp2, leftOrRight )[0];
	   range[1]=setRange(temp2, leftOrRight )[1];
	   rotate (node);
	   rotate (node);
	 
	   node.setHeight(node.getHeight()+1);
	   temp1.setHeight(temp1.getHeight()-1);
	   temp2.setHeight(temp2.getHeight()-2);
 }
   
   

   
   
   /**
    * the function checks if a given node is a left or right child
    * the function returns true if node is right child, and false if left child
    */
   public boolean leftOrRight(IAVLNode node) {
	   if (this.root.getKey()==node.getKey()) {
		   return true;
	   }
	   if (node.getParent().getRight().getKey()==node.getKey()) {
		   return true;
	   }
	   return false;
   }

   /**
    * the function set an array that indicates the type of the node
    * types possible: 1-1, 1-2, 2-1 
    * the functions return an array: array[0]= the range between the node and his parent, array[1]=the range between the parent and the other child
    *the functions uses the function leftOrRight in order to know if node is right or left child
    */
   public int[] setRange (IAVLNode node, boolean leftOrRight) {
	   int[] ranges=new int [2];
	   if ( root.getKey()==node.getKey()) { //If node is the key
		   return ranges;
	   }
	   if (leftOrRight) {  //node is a right child
		   ranges[0]=node.getParent().getHeight()-node.getHeight();  //num of the first range
		   ranges[1]=node.getParent().getHeight()-node.getParent().getLeft().getHeight(); //num of the second range
	   }
	   else { //node is a left child
		   ranges[0]=node.getParent().getHeight()-node.getHeight();  //num of the first range
		   ranges[1]=node.getParent().getHeight()-node.getParent().getRight().getHeight();  //num of the second range
	   }

	   return ranges;
	   
   }
   
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */   
   public String min()
   {
	   if (this.root==null) {  //If tree is empty, return null
			  return null;
	   }
	   return this.min.getValue(); // 
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (this.root==null) { //If the tree is empty
			  return null;
	   }
	  return this.max.getValue();
	  
   }


   
   
  public IAVLNode successor (IAVLNode curr) {
	  if (curr.getKey()==this.max.getKey()){
		  return max;
	  }
	  if (curr.getRight().isRealNode()) {    //curr has a right child 
		  curr=curr.getRight();  //go right one time
		  while (curr.getHeight()>0 && curr.getLeft().isRealNode()) { //then go all the way left 
			  curr=curr.getLeft();
		  }
		  return curr;
	  }
	 else if ((curr.getParent().getLeft().getKey())==curr.getKey()) { //curr has no right child and it is a left child 
		 return curr.getParent();
	 }
	 else { //the node is a right child and has no right child 
		 while (curr.getParent().getRight().getKey()==curr.getKey()) {   //countinue as long as the node is a right child 
			 curr=curr.getParent();
		 }
		 curr=curr.getParent(); //go one time up and left
		 return curr;
	 }
	  
  }
  
  

  public IAVLNode predeccessor (IAVLNode curr) {
	  if (curr.getKey()==this.min.getKey()){
		  return curr;
	  }
	  if (curr.getRight().isRealNode()) {    //Curr has a left child 
		  curr=curr.getLeft();  //Go left one time
		  while (curr.getHeight()>0) { //Then go all the way left 
			  curr=curr.getRight();
		  }
		  return curr;
	  }
	 else if ((curr.getParent().getRight().getKey())==curr.getKey()) { //curr has no left child and it is a right child 
		 return curr.getParent();
	 }
	 else { //The node is a left child and has no left child 
		 while (curr.getParent().getLeft().getKey()==curr.getKey()) {   //Continue as long as the node is a right child 
			 curr=curr.getParent();
		 }
		 curr=curr.getParent(); //Go one time up and left
		 return curr;
	 }
	  
  }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
 
  
  public int[] keysToArray()
  {
      if (root == null) {
    	  return new int[0];
      }
	  int counter = 1;
	   IAVLNode cur = root;
	   int[] toRet = new int[size()];
	   //find min to start from there and add it as first in array
	   cur = min;
	   toRet[0] = cur.getKey();
	   
	   //then go one by one and add to array
	   while (cur.getKey() != max.getKey()) {
		   
		   cur = successor(cur);
		   toRet[counter++] = cur.getKey();
	   }
	   
	  return toRet;
  }
  
  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
      if (root == null) {
    	  return new String[0];
      }
	  int counter = 1;
	   IAVLNode cur = root;
	   String[] toRet = new String[size()];
	   //find min to start from there and add it as first in array
	   cur = min;
	   toRet[0] = cur.getValue();
	   
	   //then go one by one and add to array
	   while (cur.getKey() != max.getKey()) {
		   
		   cur = successor(cur);
		   toRet[counter++] = cur.getValue();
	   }
	   
	  return toRet;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   if (this.root==null) { //If tree is empty return -1 
		   return -1;
	   }
	   return root.getSize();
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IAVLNode getRoot()
   {
	   return this.root;
   }

   /**
  * public string split(int x)
  *
  * splits the tree into 2 trees according to the key x. 
  * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null
  * postcondition: none
  * small tree= tree with all the keys smaller than x
  * big tree=tree with all the keys bigger than x
  */   
 public AVLTree[] split(int x)
 {
	   IAVLNode node =searchNode(x);
	   
	   IAVLNode leaf = new AVLNode (-1, null);
	   leaf.setHeight(-1);
	   leaf.setSize(0);
	   leaf.setParent(null);

	   AVLTree smallLeft = new AVLTree();
	   smallLeft.root=leaf;
	   AVLTree smallTree= new AVLTree();
	   smallTree.root=node.getLeft();
	   AVLTree bigTree = new AVLTree();
	   bigTree.root=node.getRight();
	   AVLTree bigRight= new AVLTree();
	   bigRight.root=leaf;

	   //Get max and min of the new tree
	  IAVLNode  maxBigTree=this.max; 
	  IAVLNode minSmallTree= this.min;
	  IAVLNode minBigTree=successor(node);
	  IAVLNode maxSmallTree=predeccessor(node);
	  IAVLNode temp;
	   boolean leftOrRight=leftOrRight (node);  //check if node is right child (=true) or left child (=false)
	 
	    node=node.getParent();
	   
	   while (node!=null) {
			  temp=node.getParent();
			  boolean updateLeftOrRight=leftOrRight;
			  if (temp!=null) {
				   updateLeftOrRight=leftOrRight (node);

			   }
			  
		   if (leftOrRight) { //node is a right child
			  smallLeft.root=node.getLeft();  //the first tree to join 
			  smallLeft.root.setParent(null);
			  smallTree.root.setParent(null);
			  smallTree.join(node, smallLeft);			  
			   
		   }
		   else { //node is a left child
				bigRight.root=node.getRight();  //the second tree to join 
				bigTree.root.setParent(null);
				bigRight.root.setParent(null);
				bigTree.join(node, bigRight);
		   }
		   leftOrRight=updateLeftOrRight;
		   node=temp;
		   
		   
		   
	   }
	   
	   smallTree.max=maxSmallTree;  //Update min and max of the trees
	   smallTree.min=minSmallTree;
	   bigTree.max=maxBigTree;
	   bigTree.min=minBigTree;
	   if (!smallTree.root.isRealNode()) { //empty small tree
		   smallTree.root=null;
	   }
	   if (!bigTree.root.isRealNode()) { //empty big tree
		   bigTree.root=null;
	   }
	   
	   smallTree.root.setParent(null);
	   bigTree.root.setParent(null);
	   
	   AVLTree [] output= {smallTree, bigTree}; 
	   return output;
 }

 


   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (rank difference between the tree and t)
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys()
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   

	   if (t.getRoot() == null) {
		   insert(x.getKey(),x.getValue());
		   if (root != null)
			   return root.getHeight() + 1;
		   return 1;
	   }
	   if (root == null) {
		   root = t.getRoot();
		   min = t.min;
		   max = t.max;
		   insert(x.getKey(),x.getValue());
		   if (t.root != null)
			   return t.root.getHeight() + 1;
		   return 1;
	   }

	   int toRet = Math.abs(t.getRoot().getHeight() - root.getHeight()) + 1;
	   
	   //check which one has smaller rank and assign it accordingly.
	   AVLTree shorter = t;
	   AVLTree taller = this;
	   if (t.getRoot().getHeight() > root.getHeight()) {
		   taller = t;
		   shorter = this;
	   }
	   
	   //check if new tree has bigger or smaller values then current one.
	   boolean shorterIsSmaller = false;
	   if (shorter.getRoot().getKey() < taller.getRoot().getKey())
		   shorterIsSmaller = true;

	   //update min,max
	   if (shorterIsSmaller) {
		   min = shorter.min;
		   max = taller.max;
	   }
	   else {
		   min = taller.min;
		   max = shorter.max;
	   }
	   
	   //find the place to join the trees at
	   IAVLNode cur = taller.getRoot();
	   while (cur.getHeight() > shorter.getRoot().getHeight()) {
		   cur.setSize(cur.getSize()+shorter.getRoot().getSize() + 1);
		   if (shorterIsSmaller)
			   cur = cur.getLeft();
		   else
			   cur = cur.getRight();
	   }
	   

	   //make the join
	   if (shorterIsSmaller) {
		   x.setLeft(shorter.getRoot());
		   x.setRight(cur);
		   x.setHeight(Math.max(x.getRight().getHeight(), x.getLeft().getHeight()) + 1);
		   //check if x should be root
		   if (cur.getParent() != null) {
			   x.setParent(cur.getParent());
			   cur.getParent().setLeft(x);
			   x.getLeft().setParent(x);
			   x.getRight().setParent(x);
			   root = taller.getRoot();
			   root.setParent(null);
		   }
		   else {
			   x.getLeft().setParent(x);
			   x.getRight().setParent(x);
			   root = x;
			   root.setParent(null);
		   }
	   }
	   else {
		   x.setRight(shorter.getRoot());
		   x.setLeft(cur);
		   x.setHeight(Math.max(x.getRight().getHeight(), x.getLeft().getHeight()) + 1);
		 //check if x should be root
		   if (cur.getParent() != null) {
			   x.setParent(cur.getParent());
			   cur.getParent().setRight(x);
			   x.getLeft().setParent(x);
			   x.getRight().setParent(x);
			   root = taller.getRoot();
			   root.setParent(null);
		   }
		   else {
			   x.getLeft().setParent(x);
			   x.getRight().setParent(x);
			   root = x;
			   root.setParent(null);
		   }
	   }
		x.setSize(x.getRight().getSize() + x.getLeft().getSize() + 1);
		
	   //balance the tree, same way as after insertion
	   balanceInsertion(x);

	   return toRet ;
   }

	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
    	public void setSize(int height); // sets the size of the node
    	public int getSize(); // Returns the size of the subtree under node
    	public void setKey(int key); // sets the key of the node
    	public void setValue(String value); // sets the value of the node

	}

   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
  public class AVLNode implements IAVLNode{
		
	  private int key;
	  private String value;
	  private IAVLNode right;
	  private IAVLNode left;
	  private IAVLNode parent;
	  private int height;
	  private int size;
	  
	  /*
	   * builder.
	   * 
	   * @pre: key != null, value != null
	   * @post: AVLNode(key,value)
	   */
	  public AVLNode(int key, String value) {
		  this.key = key;
		  this.value = value;
	  }
	  
	  /*
	   * @pre: object initialized by builder.
	   * @post: key
	   */
	
	  
	  public int getKey()
		{
			return key;
		}
		/*
		 * @pre: object initialized by builder.
		 * @post: value
		 */
		public String getValue()
		{
			return value; 
		}
		/*
		 * @post: left = node
		 */
		public void setLeft(IAVLNode node)
		{
			left = node;
		}
		/*
		 * @post: left child
		 */
		public IAVLNode getLeft()
		{
			return left;
		}
		/*
		 * @post: right = node
		 */
		public void setRight(IAVLNode node)
		{
			right = node;
		}
		/*
		 * @post: right child
		 */
		public IAVLNode getRight()
		{
			return right;
		}
		/*
		 * @post: parent = node
		 */
		public void setParent(IAVLNode node)
		{
			parent = node; // to be replaced by student code
		}
		/*
		 * @post: parent
		 */
		public IAVLNode getParent()
		{
			return parent;
		}
		// Returns True if this is a non-virtual AVL node
		/*
		 * @post: is it a real node?
		 */
		public boolean isRealNode()
		{
			if (height == -1) {
				return false;
			}
			return true;
		}
		/*
		 * @post: height = height
		 */
		public void setHeight(int height)
		{
			this.height = height;
		}
		/*
		 * @post: height
		 */
		public int getHeight()
		{
			return height; 
		}
		/*
		 * @post: size of subtree
		 */
		public int getSize()
		{
			return size; 
		}
		/*
		 * @post: size = size
		 */
		public void setSize(int size)
		{
			this.size = size;
		}
		public void setKey(int key)
		{
			this.key=key;
		}
		public void setValue(String value)
		{
			this.value=value;
		}
  	}


}
  

