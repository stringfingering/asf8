package computingmusic;

//import computingmusic.utils.L1;


import java.awt.Point;
import java.util.ArrayList;

import computingmusic.ChordFingering.Fingering;
import computingmusic.utils.*;

public class FingerTrail  {
	Instrument instrument;
	private int startInd;
	private int stopInd;
	ArrayList<ChordFingering.Fingering> trails;
	// System.out.println(abcS);
	//Point[] coords;
	private TPArray trailPoints; // contains all the points, for instance (0,0), (2,3), (3, 22)
	int orders;
	//private IntSet saiten;
	public Saiten saiten; // set of strings we play on
	private PointSet stopPoints; // sets of (saiten, interval)
	
	public IntSet[] saitenArray; // array with sets of intervals for each string.
	public Bowing bowing; // sequence of strings
	private IntSet heightSet;
	public int lifts;
	/**
	 * @ array of array of <saite, int> grouped by same int
	 */
	public PointGroup heightSets; // 
	/**
	 * Alist of height, Bv of strings, true = stopped
	 */
	public BvMap heightMap; // alist of height, bv of 4 strings, true = stopped at this height,
	public BvMap pitchMap; // alist of pitch, bv of 4 strings, true = playing pitch on this string
	private PointList lastPoints; // list of saite, int added last, ie with highest order
	
	
	private boolean spanNotSet;
	private int FBLo;
	private int FBHi;
	
	@SuppressWarnings("unused")
	private Point fbRange;
	
	private double logSpan;
	boolean inSpan;
	
	// features
	int nStops; // how many fingers we could be currently holding down
	
	public TopologyAnchor topology;
	
	//private FingerBoardPoints fbp;
	
	public PointList redPoints;
	public PointList grayPoints;
	
	FingerTrail (Instrument ins, int start, int stop){
		this(ins);
		startInd = start;
		stopInd = stop;
		
		
	}
	FingerTrail (Instrument ins){
		
		lifts = 0;
		nStops = 0;
		instrument = ins;
		spanNotSet = true; // open string or just one note
		FBLo = Integer.MAX_VALUE;
		FBHi = -1;
		orders = 0;
		stopPoints = new PointSet();
		trailPoints = new TPArray();
		heightSet = new IntSet();
		heightSets = new PointGroup();
		heightMap = new BvMap(); // each bv has a length of 4, though it defaults to 64.
		pitchMap = new BvMap(); 
		
		lastPoints = new PointList();
		
		
		bowing = new Bowing();
		saiten = new Saiten();	
		
		
		makeSaitenArray();
		//saiten = new IntSet();
		
		
	}
	
	public TPArray getTrailPoints()
	{
		return trailPoints;
	}
	
	/**
	 * jb
	 * @param otherTrail
	 * @return
	 */
	public boolean isExtensionOf(FingerTrail otherTrail)
	{
		return this.trailPoints.IsExtensionOf(otherTrail.trailPoints);
	}
	
	private void addSaitenHeight(Point p) {
		if (p.y != 0) {
			saitenArray[p.x].add(p.y);
		}
		
	}
	
	private void makeSaitenArray(){
		int len = instrument.nStrings;
		saitenArray = new IntSet[len];
		// initialize
		for (int i=0;i<len;i++){
			saitenArray[i] = new IntSet();
		}
	}
	
	private IntSet[] copySaitenArray(){
		int len = saitenArray.length;
		IntSet[] c = new IntSet[len];
		for (int i=0; i<len;i++){
			c[i] = saitenArray[i].copy();
		}
		return c;
	}
	
	/*
	public FingerTrail(Instrument ins, Fingering f) {
		this(ins);
		this.startInd = f.index;
		
		FingerTrail t = this.addFingering(f);
		//if (t.inSpan) 
		
	}
	*/
	
	public FingerTrail copy () {
		
	    FingerTrail c = new FingerTrail(instrument);
	    c.lifts = lifts;
	    c.startInd = startInd;
	    c.stopInd = stopInd;
	    c.nStops = nStops;
	    c.FBLo = FBLo;
	    c.FBHi = FBHi;
	    c.spanNotSet = spanNotSet;
	    c.orders = orders;
	    c.stopPoints = stopPoints.copy();
	    c.trailPoints = trailPoints.copy();
	    c.heightSet = heightSet.copy();
	    c.heightSets = heightSets.copy();
	    c.heightMap = heightMap.copy();
	    c.pitchMap = pitchMap.copy();
	    
	    // make the shallow copy of the object of type Department
	    
	    c.saiten = saiten.copy();
	    c.saitenArray = copySaitenArray();	
	    c.lastPoints = lastPoints.copy();
	    
	    c.bowing = bowing.copy();
	    
	    
	    return c;
	}

	// two sets, calld red + Gray: red is stopped (possibly open),
	// gray is finger up. 
	public void makeFingerBoardPoints(){
	
		redPoints = new PointList();
		grayPoints = new PointList();
			for (Trailpoint t : trailPoints) {
				Point p0 = t.getPoint();
				//Point p0 = new Point (t.saite, t.interval);
				if (t.fingerDown ) {
					redPoints.add(p0);
				} else {
					grayPoints.add(p0);
				}
			}
		}
	
	    
	public boolean testExtend (Fingering cf){
		
		return false;
	};
	
	public Point fbHeightRange () {
		int lo = Integer.MAX_VALUE;
		int hi = Integer.MIN_VALUE;
		boolean open = true;
		for (int h : heightSet) {
			if (h != 0) {
				open = false;
				lo = Math.min(lo, h);
				hi = Math.max(hi, h);
			}
			
			
		}
		if (open){
			return new Point (0,0);
		} else {
			return new Point (lo, hi);
		}
	}
	private void liftOccludingFingers (int order, Point[] p){
		// p is our last set of fingers.
		// for each, if there is a finger on this saite with higher interval,
		// set "Fingerdown" to false.
		// but if same, the set down.
		for (Point p1 : p) {
			for (Trailpoint tp : trailPoints){
				int bvsz = tp.contactBv.size()-1;
				if (p1.x == tp.saite ){
					if (p1.y < tp.interval ) {
						tp.fingerDown = false;
						tp.contactBv.set(order, bvsz, false);
						lifts++;
					}
					if (p1.y == tp.interval){ // we're forcing all fingers below current down
							                  // because we should recalculate the whole trajectory.
						tp.fingerDown = true;
						tp.contactBv.set(order, bvsz, true);
					}
					
				}
			}
		}
		
	}
	public void finalize(){
		for (Trailpoint tp : trailPoints){
			tp.finalizeOrders();
		}
	}
	/*
	 * count number of times 1 pitch is played in more than 1 way in this trail.
	 */
	public int countBariolations(){
		int s = 0;
		for (BvKey bk : pitchMap) {
			int card = bk.bv.cardinality();
			if (card > 1) {
				s = s + card;
			}
		}
		return s;
		
	}

	public int stoppedHeights (){
		int sz = heightMap.size();
	
		if (heightMap.hasKey(0)) { // ignore open string
			sz = sz-1;
		}
		
		return sz;
	}
	
	/*
	 * 
	 * return FingerTrail a copy of the FingerTrail with the new Fingering, OR null if the resulting
	 * trail is invalid.
	 */
	public FingerTrail makeCopyWithNewFingering(Fingering f) {
	
		//int bowKey = Bowing.keyOfSequence(ss);
		
		//System.out.println("FingerTrail addFIngering: saiten =" + ss);
		// note we do NOT want to add in fingers we already have
		// but then order has to be intset.
		FingerTrail trail = copy();
		Point[] hPts = f.stringHeightPoints(); // x=1 y=1
		Point[] pPts = f.stringPitchPoints(); // x=1 y=62
		
		// TODO cut here if there are open strings and the constraints refuse them.
		
		//System.out.println("height points: " + hPts);
		//System.out.println("pitch points: " + pPts);
		/*for(Point pt: hPts)
		{
			System.out.println("h pt: " + pt);
		}for(Point pt: pPts)
		{
			System.out.println("p pt: " + pt);
		}*/
		
		// add to pitch/string MAP
		for (Point sp : pPts) {
			trail.pitchMap.addKey(sp.y, sp.x);
		}
		
		IntList ss = new IntList(f.stringSequence);
		//System.out.println("size of added intlist: " + ss.size());//
		trail.bowing.addKey(ss);
		
		trail.lastPoints.clear();
		// array of (saite, interval)

		trail.stopInd = f.index;
		// System.out.println("Add fingering at" + stopInd);
		trail.orders++;

		for (Point p1 : hPts) {
			int fbHeight = p1.y;
			int fbSaite = p1.x;
			
			trail.lastPoints.add(p1); // for quick reference

			trail.heightMap.addSaiteInterval(p1);
		
			trail.addSaitenHeight(p1);
			trail.saiten.add(fbSaite);

			// groups of points by same height
			trail.heightSets.addByY(p1);

			
			if (fbHeight != 0) {
				trail.spanNotSet = false;
				trail.heightSet.add(fbHeight);
				trail.FBHi = Math.max(FBHi, fbHeight);
				trail.FBLo = Math.min(FBLo, fbHeight);
				
			}
			// notice we do this with OLD order.
			Trailpoint oldTP = trail.tpofPoint(p1);
			
			if (oldTP == null) {
				Trailpoint tp = new Trailpoint(orders, p1);
				if (p1.y != 0) { // open string
					trail.stopPoints.add(tp.getPoint());
				}
				trail.trailPoints.add(tp);
			} else {
				// already have this point -- just have to add when it shows up.
				//System.out.println("---------> ADDING NEW ORDER, " + orders);
				oldTP.onOrder.add(orders);
				//System.err.println("Result: " + oldTP.onOrder);
			}

			// 
		};
		
		// set range as point
		trail.fbRange = trail.fbHeightRange();
		trail.logSpan = trail.rangeLogSpan();
		
		
		if (spanNotSet) { // only thing here is open string
			trail.inSpan = true;
		} else {
			//t.inSpan = instrument.inSpan(t.FBLo, t.FBHi);
			trail.inSpan = trail.logSpan <= instrument.maxSpan;
			
			
		}
		
		
		if (!trail.inSpan) {
			//System.out.println("WARNING!!! NOT IN SPAN --" + t.FBLo + ", " + t.FBHi );
			//System.out.println(t);
			return null;
		} else if (!trail.reallyInSpan()){
			//System.out.println("WARNING!!! NOT REALLY IN SPAN --" + t.FBLo + ", " + t.FBHi );
			return null;
		} else {
		// apply to OLD order
			trail.liftOccludingFingers(orders, hPts);
			trail.makeFingerBoardPoints();
			return trail;
		}

	}
	
	
	public boolean hasPoint(Point p) {
		for (Trailpoint tp : trailPoints){
			if (tp.saite == p.x && tp.interval == p.y) {
				return true;
			}
		}
		return false;
	}
	
	// return tp with this point or null if not found
	public Trailpoint tpofPoint(Point p) {
		for (Trailpoint tp : trailPoints){
			if (tp.saite == p.x && tp.interval == p.y) {
				return tp;
			}
		}
		return null;
	}
	
	// order = time sequence
	public int lastOrder () {
		int sz = trailPoints.size();
		if (sz > 0){
			return trailPoints.get(sz-1).order;
		} else {
			return 0;
		}
	}
	
	public PointList lastPoints(){
		return this.lastPoints;
	}
	
	public int start() {
		return startInd;
	}
	public int stop(){
		return stopInd;
	}
	
	public Point io (){
		return new Point (startInd, stopInd);
	}
	public double getHandSpan(){
		return instrument.getSpan(FBLo, FBHi);
	}
	
	public PointSet getStoppedPoints(){
		return stopPoints;
	}
	public int countStops (){
		return stopPoints.size();
	}
	public int heightSetSize(){
		return heightSet.size();
	}
	
	public boolean bad (){
		// bad ion case too many fingers are required or handspan excessive.
		
		return heightSet.size() > 4 || !inSpan;
		
	}
	
	public double logHandSpan (){
		if (spanNotSet) {
			return 0;
		} else {
			//System.out.println("Hi: "+FBHi + " Low: " + FBLo);
			return  instrument.getSpan(FBHi, FBLo);
		}
	}
	
	
	public double rangeLogSpan () {
		Point range = fbHeightRange();
		return instrument.getSpan(range);
	}
	
	/**
	 * while the first condition is by Eliot, thoe others are by Jean-Benoit.
	 * 
	 * @return
	 */
	public boolean reallyInSpan () {
		boolean basicCondition = logHandSpan() <= instrument.maxSpan;
		//System.out.println("log hand span: " + logHandSpan());
		/*boolean secondCondition = heightSet.max()<=Constraints.MAX_HEIGHT && 
				heightSet.min()>=Constraints.MIN_HEIGHT; // jb*/
		
		boolean second = reallyJBApprovedInSpan();
		return basicCondition && second;
	}
	
	public boolean reallyJBApprovedInSpan()
	{
		boolean heightCondition = true;
		for(Trailpoint tp : trailPoints)
		{
			if((Constraints.VIOLIN_MIN_HEIGHTS[tp.saite] > tp.interval && tp.interval!=0) ||
					 tp.interval > Constraints.VIOLIN_MAX_HEIGHTS[tp.saite]  )
			{
				heightCondition = false;
			}
		}
		
		boolean playableCondition = saiten.usedOnlyUnderConstraints();
		boolean validOpenCondition = trailPoints.openPermittedByConstraints();
		
		boolean handSpanCondition = logHandSpan() <= instrument.getSpan(1, Constraints.VIOLIN_HAND_SPAN);
		
		return heightCondition && playableCondition && validOpenCondition && handSpanCondition;
	}
	
	@Override public String toString(){
		int bariolations = countBariolations();
		double ezspan = instrument.ezSpan;
		//boolean reallyinSpan = logHandSpan()<=instrument.maxSpan;
		boolean reallyinSpan = reallyInSpan();
		boolean ez = logHandSpan()<=ezspan;
		String s = "(" + FBLo + ", " + FBHi + ") Really in span? " 
			+ reallyinSpan + " In span? " + inSpan + "  EZ Span? " + ez 
			+ " SPANNOTSET: " + spanNotSet 
			// //Span: " + logHandSpan() + "/" + instrument.maxSpan  
			//+ "handspan slot is " + getHandSpan() 
			+ "\n---------------------\n\n";
		s = s + "Bowing: " + bowing + "\n";
		s = s + "Bariolations: " + bariolations + "\n";
		s = s + "Lifts: " + lifts + "\n";
		for (Trailpoint tp : trailPoints){
			s = s + tp + "\n";
		}
		return s;
	}
	
	public String describe () {
		//Point fbh = fbHeightRange();
		String span = String.format("%.2f/%.2f",logSpan,instrument.maxSpan);
		int bariolations = countBariolations();
		//String s = "Bowing: " + bowing + "<br>";
		String s = bowing.describe();
		s = s + "<br>Bariolations: " + bariolations + "<br>";
		s = s + "Lifts: " + lifts + "<br>";
		if (topology != null) {
			s = s + "Axes: " + topology.nAxes + "<br>";
		}
		s = s + "FB lo/hi:" + FBLo + "/" + FBHi + "<br>";
		s = s + "logspan: " + span + "<br>";
		Point range = fbHeightRange();
		s = s + "FBLO/HI new: " + range.x + "/" + range.y + "<br>";
		return s;
	}
	
	public class Saiten {
		
		private boolean[] arr;
		private int len;
		
		Saiten() {
			len = instrument.nStrings;
			arr = new boolean [len];
		}
		
		public boolean usedOnlyUnderConstraints() {
			for(int i = 0; i<len; i++)
			{
				if(arr[i]==true && Constraints.VIOLIN_PLAYABLES[i]==false)
				{
					System.out.println("NOT USED UNDER CONSTRAINTS");
					System.out.println(arr);
					System.out.println(Constraints.VIOLIN_PLAYABLES);
					return false;
				}
			}
			return true;
		}
		
		public void add (int saite) {
			// no checking
			arr[saite] = true;
		}
		public int nSaiten (){
			int c = 0;
			for (boolean b : arr) {
				if (b) c++;
			}
			return c;
		}
		public Saiten copy(){
			Saiten c = new Saiten();
			for (int i = 0; i<len; i++){
				c.arr[i] = arr[i];
				
			}
			return c;
		}
		
		@Override
		public String toString()
		{
			String s = "";
			for(boolean b : arr)
			{
				s += b ? "+" : ".";
			}
			return s;
		}
	}
	

	
	public class FTArray extends ArrayList<FingerTrail> {

		private static final long serialVersionUID = 1L;
		
	}
	public class TPArray extends ArrayList<Trailpoint>{
		
		private static final long serialVersionUID = 1L;

		/**
		 * author: JB
		 * @param previousPs a bigger PointSet 
		 * @return whether or not this PointSet is contained in the other PointSet.
		 */
		public boolean IsExtensionOf(TPArray previous)
		{
			boolean unknownPoint = false;
			for(Trailpoint pt : previous)
			{
				//System.out.println("NEW POINTS:" + this);
				//System.out.println("THIS POINT: " + pt);
				if(!this.containsCoordinates(pt))
				{
					//System.out.println("UNKNOWN POINT WAS FOUND!!!!");
					unknownPoint = true;
				}
			}
			return !unknownPoint;
		}
		
		public boolean openPermittedByConstraints() {
			boolean permit = true;
			for(Trailpoint tp : this)
			{
				if(tp.interval == 0 && !Constraints.VIOLIN_OPEN[tp.saite])
				{
					permit = false;
				}
			}
			return permit;
		}

		private boolean containsCoordinates(Trailpoint tp)
		{
			boolean contains = false;
			for(Trailpoint point : this)
			{
				if(point.saite == tp.saite && point.interval == tp.interval) // TODO add order similarity to this?
				{
					contains = true;
				}
			}
			return contains;
		}
		
		public TPArray copy () {
			TPArray c = new TPArray();
			for (Trailpoint tp: this) {
				c.add(tp.copy());
			}
			return c;
		}
	}
	
	public class Trailpoint  {
	    	//Color color;
			int index;
	    	int om;
	    	int order;
	    	IntSet onOrder;
	    	int saite;
	    	int interval; // from open string
	    	//int fbPosition;
	    	boolean fingerDown;
	    	Bv contactBv;
	    	
	    	public Trailpoint (){
	    	}
	    		
	    		
	    	
	    	public Trailpoint(int order, Point p) {
	    		om = 0;
	    		this.order = order;
	    		onOrder = new IntSet();
	    		onOrder.add(order);
	    		saite = p.x;
	    		interval = p.y; 
	    		fingerDown = true;
	    		//fbPosition = Instrument.notePos(interval);
				//color = color.BLACK; // gray();
	    		contactBv = new Bv(16); // 16 is print length;
	    		contactBv.set(orders);
	    	}
	    	
	    	public Point getPoint(){
	    		// for fingerboard
	    		return new Point(saite,interval);
	    	}
	    	public Trailpoint copy(){
	    		Trailpoint t = new Trailpoint();
	    		t.index = index;
	    		t.om = om;
	    		t.order = order;
	    		t.onOrder = onOrder.copy();
	    		t.saite = saite;
	    		t.interval = interval;
	    		t.fingerDown = fingerDown;
	    		//t.fbPosition = fbPosition;
	    		t.contactBv = contactBv.copy();
	    		return t;
	    	}
	    	
	    	public void finalizeOrders() {
	    		int lastOrder = onOrder.max();
	    		contactBv.set(lastOrder+1, (contactBv.size()-1), false); 
	    	}
	    	
	    		    		
	    	@Override
	    	public String toString(){
	    		return (contactBv + "TP/" + onOrder + " (saite:" + saite + ", interval:" + interval + ", " 
	    				+  " fingerDown: " + fingerDown +  " "  + ")");	
	    	}
	    	
	    	
	    	/*
	    	private boolean isLegato (Trailpoint tp){
	    		// only same or adjacent strings;
	    		int saiteDiff = Math.abs(saite-tp.saite);
	    		if (saiteDiff>1) return false;
	    		
	    		return true;
	    	}
	    	*/
	    	
	    }
	  
	/*
		
	public boolean isLegato (){
		return false;
	}
	*/
	  
	/*
	
	Color trail1 = new Color(0xFF6A38);
    Color trail2 = new Color(0xFF571F);
    Color trail3 = new Color(0xFFB399);
    Color[] trailColors = { trail1, trail2, trail3 };
    */
	 
}
