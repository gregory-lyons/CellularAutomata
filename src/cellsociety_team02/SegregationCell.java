package cellsociety_team02;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SegregationCell extends Cell{

	private static final int stateX = 1;
	private static final int stateY = 2;
	private static final String THRESHOLD = "threshold";
	private double threshold;
	
	public SegregationCell(double state, int x, int y,
			Map<String, String> parameters) {
		super(state, x, y, parameters);
		if(parameters.containsKey(THRESHOLD))
			threshold = Double.parseDouble(parameters.get(THRESHOLD));
		else threshold = 0.3; //Default value
	}

	@Override
	public void updateStateandMove(Cell[][] cellList, Patch[][] patches) {
		double state = currentState;
		getNeighbors(cellList);
		if(state == 0) return;
		double xNeighbors = 0;
		double yNeighbors = 0;
		
		for(Cell c: neighborsList){
			switch((int) c.getCurrentState()){
			case stateX: xNeighbors++; break;
			case stateY: yNeighbors++; break;
			}
		}
		
		if(xNeighbors + yNeighbors == 0){move(cellList); return;}
		double xRatio = xNeighbors/(xNeighbors+yNeighbors);
		double yRatio = yNeighbors/(xNeighbors+yNeighbors);
		if(!(state==stateX && xRatio>threshold) && !(state==stateY && yRatio>threshold))
			move(cellList);
		else futureState = currentState;
	}

	private void move(Cell[][] cellList) {
		List<Point2D> possibleDest = new ArrayList<>();
		for(int r=0;r<cellList.length;r++){
			for(int c=0;c<cellList[0].length;c++){
				if((cellList[r][c].getCurrentState() == 0) && (cellList[r][c].getFutureState() == 0)){
					possibleDest.add(new Point2D.Double(r,c));
				}
			}
		}
		int index = new Random().nextInt(possibleDest.size());
		int x = (int) possibleDest.get(index).getX();
		int y = (int) possibleDest.get(index).getY();
		cellList[x][y].setFutureState(currentState);
		futureState = 0;
	}

	@Override
	protected void getNeighbors(Cell[][] cellList) {
		neighborsList.clear();
		int[] xDelta = {-1, -1, -1, 0, 0, 1, 1, 1};
		int[] yDelta = {-1, 0, 1, -1, 1, -1, 0, 1};

		for(int k=0; k<xDelta.length;k++){
			if(currentX+xDelta[k]>=0 && currentX+xDelta[k] <cellList.length
					&& currentY+yDelta[k] >= 0 && currentY+yDelta[k] <cellList[0].length){
				neighborsList.add(cellList[currentX+xDelta[k]][currentY+yDelta[k]]);
			}
		}
	}

	@Override
	public Paint getColor() {
		switch((int) futureState){
		case stateX: return Color.RED;
		case stateY: return Color.BLUE;
		}
		return Color.WHITE;
	}

}