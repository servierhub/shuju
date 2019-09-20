class Brain_ {
  Model model;
  Optimizer optimizer;
  Loss criterion;
  float accuracy;
  float mean;
  boolean dataChanged;
  
  Brain_() {
    this.accuracy = 0.0;
    this.mean = 1.0;
    this.dataChanged = false;
  }

  void init(String modelName) {
    if (modelName.equals("Softmax")) {
      this.buildModelSoftmax();
    } else if (modelName.equals("Huber")) {
      this.buildModelHuber();
    } else if (modelName.equals("MSE")) {
      this.buildModelMSE();
    }
  }

  Matrix predict(PVector point) {
    Matrix input = new Matrix(new float[] { point.x, point.y });
    return this.model.model(input).detach();
  }

  void fit() {
    if (!this.dataChanged && (Map2D.points.size() == 0 || this.mean < 1e-4)) {
      this.dataChanged = false;
      return;
    }

    for (int n = 0; n < BRAIN_CLOCK; n++) {
      float sumAccu = 0.0;
      float sumMean = 0.0;

      this.optimizer.zeroGradients();

      for (int i = 0; i < Map2D.points.size(); i++) {
        PVector point = Map2D.points.get(i);

        Matrix input = new Matrix(new float[] { point.x, point.y });
        Matrix target = new Matrix(new Vector(2).oneHot(int(point.z)));

        Layer output = this.model.model(input);
        Loss loss = this.criterion.loss(output, target);
        
        if(output.detachAsVector().argmax() != target.argmax(0)) {
          loss.backward();
        } else {
          sumAccu++;
        }

        sumMean += loss.getValue().flatten(0);

        if (Float.isNaN(sumMean)) {
          sumMean = 0.0;
          println(loss.getValue());
          println(target);
          println(output.detach());
        }
      }

      this.optimizer.step();

      this.accuracy = constrain(sumAccu / Map2D.points.size(), 0, 1);
      this.mean = constrain(sumMean / Map2D.points.size(), 0, 1);
    }
  }

  void buildModelSoftmax() {
    this.model = new Model();
    
    this.model.add(new LayerBuilder()
      .setInputUnits(2)
      .setUnits(BRAIN_HIDDEN_NEURONS)
      .setActivation(new LeakyRelu())
      .setNormalizer(new BatchNormalizer())
      .build());
      
    this.model.add(new LayerBuilder()
      .setInputUnits(BRAIN_HIDDEN_NEURONS)
      .setUnits(BRAIN_HIDDEN_NEURONS)
      .setActivation(new LeakyRelu())
      .setNormalizer(new BatchNormalizer())
      .build());
      
    this.model.add(new LayerBuilder()
      .setInputUnits(BRAIN_HIDDEN_NEURONS)
      .setUnits(2)
      .setActivation(new Softmax())
      .build());
    
    this.optimizer = new OptimizerAdamBuilder().build(this.model);

    this.criterion = new Loss(new SoftmaxCrossEntropy());
  }

  void buildModelMSE() {
    this.model = new Model();
    
    this.model.add(new LayerBuilder()
      .setInputUnits(2)
      .setUnits(BRAIN_HIDDEN_NEURONS)
      .setActivation(new Tanh())
      .build());
      
    this.model.add(new LayerBuilder()
      .setInputUnits(BRAIN_HIDDEN_NEURONS)
      .setUnits(2)
      .setActivation(new Linear())
      .build());

    this.optimizer = new OptimizerSgdBuilder()
      .setLearningRate(0.1)
      .setScheduler(new ExponentialScheduler(0.0001, 1, 0.001))
      .build(this.model);

    this.criterion = new Loss(new MeanSquaredError());
  }

  void buildModelHuber() {
    this.model = new Model();
    
    this.model.add(new LayerBuilder()
      .setInputUnits(2)
      .setUnits(BRAIN_HIDDEN_NEURONS)
      .setActivation(new Relu())
      .build());
      
    this.model.add(new LayerBuilder()
      .setInputUnits(BRAIN_HIDDEN_NEURONS)
      .setUnits(2)
      .setActivation(new Linear())
      .build());

    this.optimizer = new OptimizerRMSPropBuilder().build(this.model);

    this.criterion = new Loss(new Huber());
  }
  
  String toString() {
    String result = "";
    /*
    for (Layer layer = this.model.start.next; layer != null; layer = layer.next) {
      if (layer.prev == this.model.start) {
        result += String.format("%d -> %d -> %s ->", layer.weights.W.cols, layer.weights.W.rows, getClassInfo(layer.activation));
      } else if (layer.next == null) {
        result += String.format("%d -> %s", layer.weights.W.rows, getClassInfo(layer.activation));
      } else {
        result += String.format("%d -> %s ->", layer.weights.W.rows, getClassInfo(layer.activation));
      }
    }
    */
    return result;
  }
}
Brain_ Brain = new Brain_();