/**
* Authors: Rafael Farias Pinho and Jesimar da Silva Arantes
* Date: 01/07/2018
* Last Update: 24/09/2018
* Description: Code that calculates route using algorithm A*
* Descricao: C�digo que calcula rota usando o algoritmo A*
*/

//============================USED LIBRARIES============================

#include <stdio.h>
#include <stdlib.h>
#include <float.h>
#include <math.h>

//===============================DEFINES================================

#define TRUE 1
#define FALSE 0
#define debug TRUE
#define dim_x_min -100
#define dim_x_max 100
#define dim_y_min -100
#define dim_y_max 100
#define dim_x_total 201
#define dim_y_total 201

#define map_size_rows 201
#define map_size_cols 201

//=========================STRUCTS and TYPEDEF==========================

/*
 * Struct that defines a point in the map
 */
struct point2D{
	float x;
	float y;
}typedef point2D;

/*
 * struct that defines a obstacle
 */
struct obstacle{
	int number_of_sides;
	point2D* point;
}typedef obstacle;

/* 
 * description of graph node 
 */
struct stop{
	double col, row;
	//array of indexes of routes from this stop to neighbors in array of all routes
	int *n;
	int n_len;
	double f, g, h;
	int from;
};

/* 
 * description of route between two nodes 
 */
struct route {
	//route has only one direction!
	int x; //index of stop in array of all stops of src of this route
	int y; //index of stop in array of all stops of dst of this route
	double d;
};

//===========================GLOBAL VARIABLES===========================

int ind[map_size_rows][map_size_cols];

//==============================FUNCTIONS===============================

/*
 * function that returns the beginning point
 */
point2D readPositionStart(FILE *file){
	char line[100];
	char *pend;
	int count = 0;
	point2D pointStart;
	while (!feof(file)){
		fgets(line, 100, file);
		if (count == 1){
			break;
		}
		count++;
	}
	int i = 0;
	while(line[i] != '\0'){
		if(line[i] == ';'){
			line[i] = ' ';
		}
		i++;
	}
	pointStart.x = strtof(line, &pend);
	pointStart.y = strtof(pend, NULL);
	pointStart.x = pointStart.x + 100;
	pointStart.y = pointStart.y + 100;
	return pointStart;
}

/*
 * function that returns the final point
 */
point2D readPositionGoal(FILE *file){
	char line[100];
	char* pend;
	point2D pointGoal;
	while (!feof(file)){
		fgets(line, 100, file);
	}
	int i = 0;
	while(line[i] != '\0'){
		if(line[i] == ';'){
			line[i] = ' ';
		}
		i++;
	}
	pointGoal.x = strtof(line, &pend);
	pointGoal.y = strtof(pend, NULL);
	pointGoal.x = pointGoal.x + 100;
	pointGoal.y = pointGoal.y + 100;
	//destino.x = (destino.x+5)*10;
    //destino.y = (destino.y)*10;
	return pointGoal;
}

/*
 * function that gets the points of the obstacles
 */
void readObstacles(FILE* file, obstacle* obst, int num_obstacles){
	char line[100];
	char *pend;
	int l=0;
	while(!feof(file)){
		fgets(line, 100, file);
		if(feof(file)){
			break;
		}
		fgets(line, 100, file);
		if(feof(file)){
			break;
		}
		obst[l].number_of_sides = atoi(line);
		for(int i = 0; i < obst[l].number_of_sides; i++){
			fgets(line, 100, file);
			int j = 0;
			while(line[j] != '\0'){
				if(line[j] == ';'){
					line[j] = ' ';
				}
				j++;
			}
			obst[l].point[i].x = strtof(line, &pend);
			obst[l].point[i].y = strtof(pend, NULL);
		}
		l++;
	}
	rewind(file);
	if (debug == TRUE){
		printf("Number of Obstacles = %d\n", num_obstacles);
		for(int l = 0; l < num_obstacles; l++){
			printf("  Obstacle ID: %d\n", l);
			printf("  Lado: %d\n", obst[l].number_of_sides);
			for(int i = 0; i < obst[l].number_of_sides; i++){
				printf("    (x; y) = (%f; %f)\n", obst[l].point[i].x, obst[l].point[i].y);
			}
		}
	}
}

/*
 * function that draws the obstacles in the map
 */
void fill_map(char map[map_size_rows][map_size_cols], obstacle* obst, int num_obstacles){
	for (int i = 0; i < map_size_rows; i++){
		for(int j = 0; j < map_size_cols; j++){
			map[i][j] = 0;
		}
	}
	
	int obsx[100], obsy[100];
	for(int i = 0; i < num_obstacles; i++){
		//if (debug == TRUE){
		//	printf("i = %d\n", i);
		//	printf("  l = %d\n", obst[i].number_of_sides);
		//}
		for(int j = 0; j < obst[i].number_of_sides; j++){
			obsx[j] = (int)(obst[i].point[j].x);
			obsy[j] = (int)(obst[i].point[j].y);
			obsx[j] = obsx[j] + 100;
			obsy[j] = obsy[j] + 100;
			//if (debug == TRUE){//imprime os valores das coordenadas dos obstaculos sendo feitas as devidas correcoes para a matriz
				//printf("    inteiros(j=%d    x=%d    y=%d)\n", j, obsx[j], obsy[j]);
			//}
		}
		
		//draws each side of the first obstacle one by one, then it repeats the process to nest one
		for(int l = 0; l < obst[i].number_of_sides; l++){
			int prox = l + 1;
			if(l == obst[i].number_of_sides - 1){
				prox = 0;
			}
			if(obsx[l] <= obsx[prox] && obsy[l] <= obsy[prox]){
				for(int abs = obsx[l]; abs <= obsx[prox]; abs++){
					for(int ord=obsy[l]; ord <= obsy[prox]; ord++){
						if((ord-obsy[l])*(obsx[prox]-obsx[l]) == (obsy[prox]-obsy[l])*(abs-obsx[l])){
							map[ord][abs] = 1;
						}
					}
				}
			} else if(obsx[l] <= obsx[prox] && obsy[l] >= obsy[prox]){
				for(int abs = obsx[l]; abs <= obsx[prox]; abs++){
					for(int ord = obsy[l]; ord >= obsy[prox]; ord--){
						if((ord-obsy[l])*(obsx[prox]-obsx[l]) == (obsy[prox]-obsy[l])*(abs-obsx[l])){
							map[ord][abs] = 1;
						}
					}
				}
			} else if(obsx[l] >= obsx[prox] && obsy[l] <= obsy[prox]){
				for(int abs = obsx[l]; abs >= obsx[prox]; abs--){
					for(int ord=obsy[l];ord<=obsy[prox];ord++){
						if((ord-obsy[l])*(obsx[prox]-obsx[l]) == (obsy[prox]-obsy[l])*(abs-obsx[l])){
							map[ord][abs] = 1;
						}
					}
				}
			} else if(obsx[l] >= obsx[prox] && obsy[l] >= obsy[prox]){
				for(int abs = obsx[l]; abs >= obsx[prox]; abs--){
					for(int ord=obsy[l]; ord>= obsy[prox]; ord--){
						if((ord-obsy[l])*(obsx[prox]-obsx[l]) == (obsy[prox]-obsy[l])*(abs-obsx[l])){
							map[ord][abs] = 1;
						}
					}
				}
			}
		} 
	}
}

void printMap(char map[map_size_rows][map_size_cols], int p_len, int *path){
	FILE* file_map = fopen("file_map.txt", "w");
	int isFirst = TRUE;
	for (int i = 0 ; i < map_size_rows; i++) {
		for (int j = 0; j < map_size_cols; j++) {
			if (map[i][j] == 1) {
				fprintf(file_map, "o");
			} else {
				int b = 0;
				for (int k = 0; k < p_len; k++) {
					if (ind[i][j] == path[k]) {
						b++;
					}
				}
				if (b == 0) {
					fprintf(file_map, ".");
				} else {
					if (isFirst == TRUE){
						fprintf(file_map, "S");
						isFirst = FALSE;
					}else{
						fprintf(file_map, "x");
					}
				}
			}
		}
		fprintf(file_map, "\n");
	}
	fclose(file_map);
}

void a_star(char map[map_size_rows][map_size_cols], point2D pointStart, point2D pointGoal){
	for(int i = 0; i <= map_size_rows-1; i++){
		for(int j = 0; j <= map_size_cols-1; j++){
			ind[i][j]=-1;
		}
	}
	int found;
	int p_len = 0;
	int *path = NULL;
	int c_len = 0;
	int *closed = NULL;
	int o_len = 1;
	int *open = (int*)calloc(o_len, sizeof(int));
	double min, tempg;
	int current;
	int s_len = 0;
	struct stop * stops = NULL;
	int r_len = 0;
	struct route * routes = NULL;

	for (int i = 1; i < map_size_rows - 1; i++) {
		for (int j = 1; j < map_size_cols - 1; j++) {
			if (!map[i][j]) {
				++s_len;
				stops = (struct stop *)realloc(stops, s_len * sizeof(struct stop));
				int t = s_len - 1;
				stops[t].col = j;
				stops[t].row = i;
				stops[t].from = -1;
				stops[t].g = DBL_MAX;
				stops[t].n_len = 0;
				stops[t].n = NULL;
				ind[i][j] = t;
			}
		}
	}

	//index of start stop
	//int s = ((int)(pointStart.x) + 1) * (int)(pointStart.y + 1);//calculo rafael
	int s = dim_x_total * pointStart.y + pointStart.x - 3 * 200 +1;//calculo jesimar
	if (debug == TRUE){
		printf("valor s = %d\n", s);
	}
	
	//index of finish stop
	//int e = s_len - (map_size_rows - (int)(pointGoal.x)) * (map_size_cols - (int)(pointGoal.y));//calculo rafael
	int e = dim_x_total * (pointGoal.y - 1) + (pointGoal.x);//calculo jesimar
	if (debug == TRUE){
		printf("valor e = %d\n", e);
		printf("valor s_len = %d\n", s_len);
	}

	for (int i = 0; i < s_len; i++) {
		stops[i].h = sqrt(pow(stops[e].row - stops[i].row, 2) + pow(stops[e].col - stops[i].col, 2));
	}

	for (int i = 1; i < map_size_rows - 1; i++) {
		for (int j = 1; j < map_size_cols - 1; j++) {
			if (ind[i][j] >= 0) {
				for (int k = i - 1; k <= i + 1; k++) {
					for (int l = j - 1; l <= j + 1; l++) {
						if ((k == i) && (l == j)) {
							continue;
						}
						if (ind[k][l] >= 0) {
							++r_len;
							routes = (struct route *)realloc(routes, r_len * sizeof(struct route));
							int t = r_len - 1;
							routes[t].x = ind[i][j];
							routes[t].y = ind[k][l];
							routes[t].d = sqrt(pow(stops[routes[t].y].row - stops[routes[t].x].row, 2) + pow(stops[routes[t].y].col - stops[routes[t].x].col, 2));
							++stops[routes[t].x].n_len;
							stops[routes[t].x].n = (int*)realloc(stops[routes[t].x].n, stops[routes[t].x].n_len * sizeof(int));
							stops[routes[t].x].n[stops[routes[t].x].n_len - 1] = t;
						}
					}
				}
			}
		}
	}
	open[0] = s;
	stops[s].g = 0;
	stops[s].f = stops[s].g + stops[s].h;
	found = 0;
	while (o_len && !found) {
		min = DBL_MAX;
		for (int i = 0; i < o_len; i++) {
			if (stops[open[i]].f < min) {
				current = open[i];
				min = stops[open[i]].f;
			}
		}
		if (current == e) {
			found = 1;
			++p_len;
			path = (int*)realloc(path, p_len * sizeof(int));
			path[p_len - 1] = current;
			while (stops[current].from >= 0) {
				current = stops[current].from;
				++p_len;
				path = (int*)realloc(path, p_len * sizeof(int));
				path[p_len - 1] = current;
			}
		}
		for (int i = 0; i < o_len; i++) {
			if (open[i] == current) {
				if (i != (o_len - 1)) {
					for (int j = i; j < (o_len - 1); j++) {
						open[j] = open[j + 1];
					}
				}
				--o_len;
				open = (int*)realloc(open, o_len * sizeof(int));
				break;
			}
		}

		++c_len;
		closed = (int*)realloc(closed, c_len * sizeof(int));
		closed[c_len - 1] = current;

		for (int i = 0; i < stops[current].n_len; i++) {
			int b = 0;
			for (int j = 0; j < c_len; j++) {
				if (routes[stops[current].n[i]].y == closed[j]) {
					b = 1;
				}
			}
			if (b) {
				continue;
			}
			tempg = stops[current].g + routes[stops[current].n[i]].d;
			b = 1;
			if (o_len > 0) {
				for (int j = 0; j < o_len; j++) {
					if (routes[stops[current].n[i]].y == open[j]) {
						b = 0;
					}
				}
			}
			if (b || (tempg < stops[routes[stops[current].n[i]].y].g)) {
				stops[routes[stops[current].n[i]].y].from = current;
				stops[routes[stops[current].n[i]].y].g = tempg;
				stops[routes[stops[current].n[i]].y].f = stops[routes[stops[current].n[i]].y].g + stops[routes[stops[current].n[i]].y].h;
				if (b) {
					++o_len;
					open = (int*)realloc(open, o_len * sizeof(int));
					open[o_len - 1] = routes[stops[current].n[i]].y;
				}
			}
		}
	}

	if (!found) {
		printf("IMPOSSIBLE\n");
	} else {
		printf("\npath cost is %d:\n", p_len);
		printf("\nRoute com coordenadas do convertidas - mesmas do MAPA visual\n");
		FILE* outputroute = fopen("output.txt", "w");
		for (int i = p_len - 1; i >= 0; i--) {
			printf("(x, y) = (%1.0f, %1.0f)\n", stops[path[i]].col, stops[path[i]].row);
			fprintf(outputroute, "%.2f;%0.2f\n", stops[path[i]].col, stops[path[i]].row);
		}
		fclose(outputroute);
		
		printf("\nRoute com coordenadas normais - mesmas do Sistema MOSA\n");
		FILE* outputroute2 = fopen("output2.txt", "w");
		for (int i = p_len - 1; i >= 0; i--) {
			printf("(x, y) = (%1.0f, %1.0f)\n", stops[path[i]].col - 100, stops[path[i]].row - 100);
			fprintf(outputroute2, "%.2f;%0.2f\n", stops[path[i]].col - 100, stops[path[i]].row - 100);
		}
		fclose(outputroute2);
		printMap(map, p_len, path);
	}

	for (int i = 0; i < s_len; ++i) {
		free(stops[i].n);
	}
	free(stops);
	free(routes);
	free(path);
	free(open);
	free(closed);
}

int main(){
	int num_obstacles = 0;
	char line[100];

	FILE *fileGoals = fopen("goals.txt" , "r");
	point2D pointStart = readPositionStart(fileGoals);
	point2D pointGoal = readPositionGoal(fileGoals);
	fclose(fileGoals);
	if (debug == TRUE){
		printf("Point Start (x, y) = (%.2f %.2f)\n", pointStart.x, pointStart.y);
		printf("Point Goal  (x, y) = (%.2f %.2f)\n", pointGoal.x, pointGoal.y);
	}

	FILE *fileObstacles = fopen("map-nfz-astar.sgl","r");//fopen("obstaculos2.txt","r");//fopen("map-nfz-astar.sgl","r");
	
	//in this part fileObstacles the code, is defined the number of obstacles
	while(!feof(fileObstacles)){
		fgets(line, 100, fileObstacles);
		if(feof(fileObstacles)){
			break;
		}
		fgets(line, 100, fileObstacles);
		if(feof(fileObstacles)){
			break;
		}
		int aux = atoi(line);
		num_obstacles = num_obstacles + 1;
		for(int n = 0; n < aux; n++){
			fgets(line, 100, fileObstacles);
			if(feof(fileObstacles)){
				break;
			}
		}
	}
	
	obstacle obst[num_obstacles];

	rewind(fileObstacles);
	int max_points_poly = 100;
	for(int x = 0; x < num_obstacles; x++){
		obst[x].point = (point2D*) malloc(max_points_poly * sizeof(point2D));
	}
	readObstacles(fileObstacles, obst, num_obstacles);
	fclose(fileObstacles);
	
	char map[map_size_rows][map_size_cols];
	
	
	fill_map(map, obst, num_obstacles);
	a_star(map, pointStart, pointGoal);
	
	return 0;
}
