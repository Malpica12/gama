/**
* Name: Agent Based Clustering
* Author: Jean-Danie Zucker with Patrick Taillandier's and Arnaud Grignard's Help
* Description: This model displays the step to stpe algorithm of k-means
* See for  https://en.wikipedia.org/wiki/K-means_clustering ...
* Clustering
* The k-medoid could be added
* To be added stop the simulation when convergence is reached
* To be added an overlay
* To be added position the points at the begining usug user interaction model...
*/
model MASKMEANS


global
{
// the number of classes to create (kmeans)
// It corresponds to the centroids
	int k <- 0;
	// the number of points
	int N <- 0;
	//number of dimensions
	int dimensions;
	init
	{
		//create datapoints agents
		create datapoints number: N
		{
			if (dimensions = 3)
			{
				location <- { rnd(100), rnd(100), rnd(100) };
			}

			if (dimensions = 2)
			{
				location <- { rnd(100), rnd(100) };
			}

		}

		//create centroid agents
		create centroids number: k
		{
			if (dimensions = 3)
			{
				location <- { rnd(100), rnd(100), rnd(100) };
			}

			if (dimensions = 2)
			{
				location <- { rnd(100), rnd(100) };
			}

		}
		int K <- length(centroids);
		if (K > 0) {loop i from:0 to: K-1 { ask centroids[i] { color_kmeans  <- hsb(i/K,1,1); }}}
					
					
			//give a random color to each centroid (i.e. to each datapoints agents of the group)
			//		loop c over: centroids { rgb col <- rnd_color(255); ask c { color_kmeans <- col;}}
		
	
	}
	reflex assign_points_to_centroid when: even(cycle)
	{
	    // The "assignment" step is also referred to as expectation step,
		ask centroids
		{
			mypoints <- list<datapoints> ([]);
		}

		loop pt over: datapoints
		{
			ask pt
			{
				if not empty(centroids) {
					mycenter <- centroids closest_to self;
					color_kmeans <- mycenter.color_kmeans;
					add self to: mycenter.mypoints;
				}
			}

		}

	}

	reflex update_centroids when: not even(cycle)
	{
	// the "update step" as maximization step,
	// making this algorithm a variant of the generalized expectation-maximization algorithm.

	//We give a random color to each group (i.e. to each datapoints agents of the group)
		ask centroids where (not empty(each.mypoints))
		{
			location <- mean(mypoints collect each.location);
		}

	}

}

species datapoints
{
	rgb color_kmeans <- rgb(225,225,225) 	;
	centroids mycenter;
	aspect kmeans_aspect2D
	{
		draw circle(2) color: color_kmeans border:color_kmeans-25;
	}

	aspect kmeans_aspect3D
	{
		draw sphere(2) color: color_kmeans ;
	}

}

species centroids
{
	rgb color_kmeans <-  rgb(225,225,225);
	list<datapoints> mypoints;
	aspect kmeans_aspect2D
	{
		
		loop pt over: mypoints
		{
			draw line([location, pt]) + 0.1 color: color_kmeans;
		}
		draw cross(3, 0.5) color: color_kmeans border:color_kmeans-25;
	}

	aspect kmeans_aspect3D
	{
		loop pt over: mypoints
		{
			draw line([location, pt], 0.2) color: color_kmeans;
		}
		draw cube(5) color: color_kmeans border: # black;
		
	}

}

experiment clustering2D type: gui
{
	parameter "Number of clusters to split the data into" var: k init:4 category: "KMEANS";
	parameter "Number of points to be clustered" var: N init: 500;
	parameter "Number of dimensions" var: dimensions init: 2 min: 2 max: 2;
	font regular <- font("Helvetica", 14, # bold);
		
	point target <- { 20, 95 };
	output
	{
		display map_kmeans
		{
			species datapoints aspect: kmeans_aspect2D transparency:0.4;
			species centroids aspect: kmeans_aspect2D;
			graphics "Full target"
			{
				draw rectangle(100, 4) color: # yellow at: target + { 30, 0 };
				if (not even(cycle))
				{
				// the "update step" as maximization step, (a mean is done to recenter)
					draw "Current step was an estimation Step (each point is assigned the color of his nearest centroid" at: target + { 0, 15 } font: regular color: # black;
					draw "Next step is maximisation step the centroid will move to the center of its  associated points" at: target + { 0, 0 } font: regular color: # red;
				} else
				{
					draw "Current step was a maximisation step the centroid moved to the center of its associated points" at: target + { 0, 15 } font: regular color: # black;
					draw "Next step is estimation Step (each point is assigned the color of his nearest centroid" at: target + { 0, 0 } font: regular color: # green;
				}

			}

		}

	}
}

experiment clustering3D type: gui
{
	parameter "Number of clusters to split the data into" var: k init:4 category: "KMEANS";
	parameter "Number of points to be clustered" var: N init:200 ;
	font regular <- font("Helvetica", 14, # bold);
	point target <- { 20, 95 };
	parameter "Number of dimensions" var: dimensions init: 3 min: 3 max: 3;
	action _init_ {
		create MASKMEANS_model with: [dimensions::3, N::1000];
	}
	output
	{
		display map_kmeans type: opengl
		{
			species datapoints aspect: kmeans_aspect3D transparency:0.4;
			species centroids aspect: kmeans_aspect3D;
		}

	}

}
