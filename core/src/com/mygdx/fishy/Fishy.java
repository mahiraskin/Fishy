package com.mygdx.fishy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Fishy extends Game {
	private File file;
	public static BitmapFont font;
	private OrthographicCamera camera;
	public static SpriteBatch batch;
	private Texture img;
	private Pixmap cursor;
	private FreeTypeFontGenerator fontGenerator;
	private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
	private static TimerTask action;
	private static Timer cont;
	private float timeSeconds = 0f;
	private float period = 5f;
	static BufferedReader br;
	static FileInputStream fs;
	static List<Word> words = new ArrayList<Word>();
	static List<String> inputs = new ArrayList<String>();
	static int stop = 0;
	static int indextemp;
	static int indexwords = -1;
	static String[] temp;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);

		Pixmap cursor = new Pixmap(Gdx.files.internal("cursor.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, 0, 0));

		//Create a font
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("chalk.ttf"));
		fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.borderWidth = 1;
		fontParameter.borderColor = Color.BLACK;
		fontParameter.color = Color.WHITE;
		font = fontGenerator.generateFont(fontParameter);
		font.getData().setScale(8,8);

		try {
			fs= new FileInputStream("/Users/mahir/Fishy/android/assets/AllTomorrows.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		br = new BufferedReader(new InputStreamReader(fs));

		inputs.add("start");

		action = new TimerTask() {
			public void run() {
				movement();
			}

		};

		cont = new Timer();

		//choice'e ne gönderildiği önemli olacak
		next(1);

	}

	public static void next(int choice) {
		int pox = 0;
		int poy = 0;
		int others = 0;
		String[] next;
		words.clear();

		for(int i = 0; i < choice-1; i++) {
			try {
				br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String lineIWant = null;
		try {
			lineIWant = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		temp = lineIWant.split(" ", 0);

		for(int a = 0; a < temp.length; a++) {
			if (temp[a].equals("/")) {
				poy -= 100;
				pox = 0;
				others++;
				continue;
			}
			else if (temp[a].equals("_")) {
				pox += 100;
				others++;
				continue;
			}
			next = temp[a].split(":", 0);

			//next[0] harfleri say(length) ve boşluğu ona göre oluştur (öncekiyle harf farkı çarpı 200)

			if (next[1].equals("9")) {
				//String temptedeki indexini al
				indextemp = a;
				indexwords = a - others;
			}
			/*next sıfırsa farklı değilse farklı bir word ekle sıfır değilse renkli olsun
			ikinci bir constructor olbailir, ya da class içinde yapabilirsin*/
			words.add(new Word(next[0], 100 + pox, 1000 + poy, next[1], font));
			pox+=500;
		}

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		timeSeconds +=Gdx.graphics.getDeltaTime();
		if(timeSeconds > period){
			timeSeconds-=period;
			movement();
			inputs.clear();
		}

		for (Word font : words)
			font.update(batch, camera);

		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			if (inputs.size() == 0 || !inputs.get(inputs.size() - 1).equals("up"))
				inputs.add("up");
		}

		else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			if (inputs.size() == 0 || !inputs.get(inputs.size() - 1).equals("down"))
				inputs.add("down");
		}

		else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if (inputs.size() == 0 || !inputs.get(inputs.size() - 1).equals("left"))
				inputs.add("left");
		}

		else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (inputs.size() == 0 || !inputs.get(inputs.size() - 1).equals("right"))
				inputs.add("right");
		}
		batch.end();
	}

	public void movement() {
		// kelime syısının words.length() kökünü alarak da sayı belirleyebilirsin 9
		String current;
		System.out.println("it just works");
		System.out.println(inputs);
		//temp ve indexi kullan / ve _ leri sayma
        for (int currint = 0; currint < inputs.size(); currint++) {
        	current = inputs.get(currint);
       	    switch(current){
			    case "up":
					int round = indexwords - 4;
			    	for(; round >= 0; round-=4) {
						if (!words.get(round).isPassable() || words.get(round).getNext() == 9)
							break;
						words.get(round).pass();
						//bu işe yarıyor
						if (words.get(round).getNext() != 0 && words.get(round).isPassed())
							next(words.get(round).getNext());
						indexwords = round;
					}
				    break;
			    case "down":
			    	System.out.println(indexwords);
					round = indexwords + 4;
					for(; round <= 15; round+=4) {
						if (!words.get(round).isPassable() || words.get(round).getNext() == 9)
							break;
						words.get(round).pass();
						if (words.get(round).getNext() != 0 && words.get(round).isPassed())
							Fishy.next(words.get(round).getNext());
						indexwords = round;
					}
				    break;
			    case "left":
					boolean breakCommand = false;
					round = indexwords - 1;
					for(; round <= 15; round-=1) {
						if (((round) % 4) == 3)
							break;
						if (breakCommand)
							break;
						try {
							if (!words.get(round).isPassable() || words.get(round).getNext() == 9) {
								breakCommand = true;
								break;
							}
							words.get(round).pass();
							if (words.get(round).getNext() != 0 && words.get(round).isPassed())
								Fishy.next(words.get(round).getNext());
							indexwords = round;
						} catch (ArrayIndexOutOfBoundsException e) {
							break;
						}
					}
				    break;
			    case "right":
			    	//biraz sıkıntılı
					System.out.println(indexwords);
					round = indexwords + 1;
					System.out.println(round);
					for(round = indexwords + 1; ((round - 1) % 4) < 3 && round < 16; round+=1) {
						words.get(round).pass();
						if (!words.get(round).isPassable() || words.get(round).getNext() == 9) {
							break;
						}
						words.get(round).pass();
						if (words.get(round).getNext() != 0 && words.get(round).isPassed())
							Fishy.next(words.get(round).getNext());
					}
					indexwords = round;
					System.out.println(indexwords);
				    break;
			    default :
			     	break;
		  }
	   }
	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		img.dispose();
		cursor.dispose();
	}

}
