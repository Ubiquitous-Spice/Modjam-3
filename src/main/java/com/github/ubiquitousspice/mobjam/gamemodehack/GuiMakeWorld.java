package com.github.ubiquitousspice.mobjam.gamemodehack;

import com.github.ubiquitousspice.mobjam.Constants;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiMakeWorld extends GuiCreateWorld
{
	private GuiScreen parentGuiScreen;
	private GuiTextField textboxWorldName;
	private GuiTextField textboxSeed;
	private String folderName;

	/**
	 * hardcore', 'creative' or 'survival
	 */
	private String gameMode = "survival";
	private boolean generateStructures = true;
	private boolean commandsAllowed;

	/**
	 * True iif player has clicked buttonAllowCommands at least once
	 */
	private boolean commandsToggled;

	/**
	 * toggles when GUIButton 7 is pressed
	 */
	private boolean bonusItems;

	/**
	 * True if and only if gameMode.equals("hardcore")
	 */
	private boolean isHardcore;
	private boolean createClicked;

	/**
	 * True if the extra options (Seed box, structure toggle button, world type button, etc.) are being shown
	 */
	private boolean moreOptions;

	/**
	 * The GUIButton that you click to change game modes.
	 */
	private GuiButton buttonGameMode;

	/**
	 * The GUIButton that you click to get to options like the seed when creating a world.
	 */
	private GuiButton moreWorldOptions;

	/**
	 * The GuiButton in the 'More World Options' screen. Toggles ON/OFF
	 */
	private GuiButton buttonGenerateStructures;
	private GuiButton buttonBonusItems;

	/**
	 * The GuiButton in the more world options screen.
	 */
	private GuiButton buttonWorldType;
	private GuiButton buttonAllowCommands;

	/**
	 * GuiButton in the more world options screen.
	 */
	private GuiButton buttonCustomize;

	/**
	 * The first line of text describing the currently selected game mode.
	 */
	private String gameModeDescriptionLine1;

	/**
	 * The second line of text describing the currently selected game mode.
	 */
	private String gameModeDescriptionLine2;

	/**
	 * The current textboxSeed text
	 */
	private String seed;

	/**
	 * E.g. New World, Neue Welt, Nieuwe wereld, Neuvo Mundo
	 */
	private String localizedNewWorldText;
	private int worldTypeId;

	/**
	 * If the world name is one of these, it'll be surrounded with underscores.
	 */
	private static final String[] ILLEGAL_WORLD_NAMES =
			new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6",
						  "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8",
						  "LPT9"};

	public GuiMakeWorld(GuiScreen parent)
	{
		super(parent);
		this.parentGuiScreen = parent;
		this.seed = "";
		this.localizedNewWorldText = I18n.getString("selectWorld.newWorld");
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen()
	{
		this.textboxWorldName.updateCursorCounter();
		this.textboxSeed.updateCursorCounter();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.getString("selectWorld.create")));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.getString("gui.cancel")));
		this.buttonList.add(this.buttonGameMode = new GuiButton(2, this.width / 2 - 75, 115, 150, 20,
																I18n.getString("selectWorld.gameMode")));
		this.buttonList.add(this.moreWorldOptions = new GuiButton(3, this.width / 2 - 75, 187, 150, 20,
																  I18n.getString("selectWorld.moreWorldOptions")));
		this.buttonList.add(this.buttonGenerateStructures = new GuiButton(4, this.width / 2 - 155, 100, 150, 20,
																		  I18n.getString("selectWorld.mapFeatures")));
		this.buttonGenerateStructures.drawButton = false;
		this.buttonList.add(this.buttonBonusItems = new GuiButton(7, this.width / 2 + 5, 151, 150, 20, I18n.getString("selectWorld.bonusItems")));
		this.buttonBonusItems.drawButton = false;
		this.buttonList.add(this.buttonWorldType = new GuiButton(5, this.width / 2 + 5, 100, 150, 20, I18n.getString("selectWorld.mapType")));
		this.buttonWorldType.drawButton = false;
		this.buttonList.add(this.buttonAllowCommands = new GuiButton(6, this.width / 2 - 155, 151, 150, 20,
																	 I18n.getString("selectWorld.allowCommands")));
		this.buttonAllowCommands.drawButton = false;
		this.buttonList.add(this.buttonCustomize = new GuiButton(8, this.width / 2 + 5, 120, 150, 20, I18n.getString("selectWorld.customizeType")));
		this.buttonCustomize.drawButton = false;
		this.textboxWorldName = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.textboxWorldName.setFocused(true);
		this.textboxWorldName.setText(this.localizedNewWorldText);
		this.textboxSeed = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.textboxSeed.setText(this.seed);
		this.func_82288_a(this.moreOptions);
		this.makeUseableName();
		this.updateButtonText();
	}

	/**
	 * Makes a the name for a world save folder based on your world name, replacing specific characters for _s and
	 * appending -s to the end until a free name is available.
	 */
	private void makeUseableName()
	{
		this.folderName = this.textboxWorldName.getText().trim();

		for (char c0 : ChatAllowedCharacters.allowedCharactersArray)
		{
			this.folderName = this.folderName.replace(c0, '_');
		}

		if (MathHelper.stringNullOrLengthZero(this.folderName))
		{
			this.folderName = "World";
		}

		this.folderName = func_73913_a(this.mc.getSaveLoader(), this.folderName);
	}

	private void updateButtonText()
	{
		this.buttonGameMode.displayString = I18n.getString("selectWorld.gameMode") + " " + I18n.getString("selectWorld.gameMode." + this.gameMode);
		this.gameModeDescriptionLine1 = I18n.getString("selectWorld.gameMode." + this.gameMode + ".line1");
		this.gameModeDescriptionLine2 = I18n.getString("selectWorld.gameMode." + this.gameMode + ".line2");
		this.buttonGenerateStructures.displayString = I18n.getString("selectWorld.mapFeatures") + " ";

		if (this.generateStructures)
		{
			this.buttonGenerateStructures.displayString = this.buttonGenerateStructures.displayString + I18n.getString("options.on");
		}
		else
		{
			this.buttonGenerateStructures.displayString = this.buttonGenerateStructures.displayString + I18n.getString("options.off");
		}

		this.buttonBonusItems.displayString = I18n.getString("selectWorld.bonusItems") + " ";

		if (this.bonusItems && !this.isHardcore)
		{
			this.buttonBonusItems.displayString = this.buttonBonusItems.displayString + I18n.getString("options.on");
		}
		else
		{
			this.buttonBonusItems.displayString = this.buttonBonusItems.displayString + I18n.getString("options.off");
		}

		this.buttonWorldType.displayString = I18n.getString("selectWorld.mapType") + " " +
											 I18n.getString(WorldType.worldTypes[this.worldTypeId].getTranslateName());
		this.buttonAllowCommands.displayString = I18n.getString("selectWorld.allowCommands") + " ";

		if (this.commandsAllowed && !this.isHardcore)
		{
			this.buttonAllowCommands.displayString = this.buttonAllowCommands.displayString + I18n.getString("options.on");
		}
		else
		{
			this.buttonAllowCommands.displayString = this.buttonAllowCommands.displayString + I18n.getString("options.off");
		}
	}

	public static String func_73913_a(ISaveFormat saveFormat, String str)
	{
		str = str.replaceAll("[\\./\"]", "_");
		String[] word = ILLEGAL_WORLD_NAMES;
		int i = word.length;

		for (String s1 : word)
		{
			if (str.equalsIgnoreCase(s1))
			{
				str = "_" + str + "_";
			}
		}

		while (saveFormat.getWorldInfo(str) != null)
		{
			str = str + "-";
		}

		return str;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled)
		{
			if (par1GuiButton.id == 1)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
			else if (par1GuiButton.id == 0)
			{
				this.mc.displayGuiScreen(null);

				if (this.createClicked)
				{
					return;
				}

				this.createClicked = true;
				long i = (new Random()).nextLong();
				String s = this.textboxSeed.getText();

				if (!MathHelper.stringNullOrLengthZero(s))
				{
					try
					{
						long j = Long.parseLong(s);

						if (j != 0L)
						{
							i = j;
						}
					}
					catch (NumberFormatException numberformatexception)
					{
						i = (long) s.hashCode();
					}
				}

				WorldType.worldTypes[this.worldTypeId].onGUICreateWorldPress();

				EnumGameType enumgametype = EnumGameType.getByName(this.gameMode);
				WorldSettings worldsettings =
						new WorldSettings(i, enumgametype, this.generateStructures, this.isHardcore, WorldType.worldTypes[this.worldTypeId]);
				worldsettings.func_82750_a(this.generatorOptionsToUse);

				if (this.bonusItems && !this.isHardcore)
				{
					worldsettings.enableBonusChest();
				}

				if (this.commandsAllowed && !this.isHardcore)
				{
					worldsettings.enableCommands();
				}

				this.mc.launchIntegratedServer(this.folderName, this.textboxWorldName.getText().trim(), worldsettings);
				this.mc.statFileWriter.readStat(StatList.createWorldStat, 1);
			}
			else if (par1GuiButton.id == 3)
			{
				this.func_82287_i();
			}
			else if (par1GuiButton.id == 2)
			{
				if (this.gameMode.equals("survival"))
				{
					if (!this.commandsToggled)
					{
						this.commandsAllowed = false;
					}

					this.isHardcore = false;
					this.gameMode = "hardcore";
					this.isHardcore = true;
					this.buttonAllowCommands.enabled = false;
					this.buttonBonusItems.enabled = false;
					this.updateButtonText();
				}
				else if (this.gameMode.equals("hardcore"))
				{
					if (!this.commandsToggled)
					{
						this.commandsAllowed = false;
					}

					this.isHardcore = false;
					this.gameMode = Constants.GAMEMODE;
					this.isHardcore = true;
					this.buttonAllowCommands.enabled = false;
					this.buttonBonusItems.enabled = false;
					this.updateButtonText();
				}
				// OUR GAMEMODE HERE
				else if (this.gameMode.equals(Constants.GAMEMODE))
				{
					if (!this.commandsToggled)
					{
						this.commandsAllowed = true;
					}

					this.isHardcore = false;
					this.gameMode = "creative";
					this.updateButtonText();
					this.isHardcore = false;
					this.buttonAllowCommands.enabled = true;
					this.buttonBonusItems.enabled = true;
				}
				else
				{
					if (!this.commandsToggled)
					{
						this.commandsAllowed = false;
					}

					this.gameMode = "survival";
					this.updateButtonText();
					this.buttonAllowCommands.enabled = true;
					this.buttonBonusItems.enabled = true;
					this.isHardcore = false;
				}

				this.updateButtonText();
			}
			else if (par1GuiButton.id == 4)
			{
				this.generateStructures = !this.generateStructures;
				this.updateButtonText();
			}
			else if (par1GuiButton.id == 7)
			{
				this.bonusItems = !this.bonusItems;
				this.updateButtonText();
			}
			else if (par1GuiButton.id == 5)
			{
				++this.worldTypeId;

				if (this.worldTypeId >= WorldType.worldTypes.length)
				{
					this.worldTypeId = 0;
				}

				while (WorldType.worldTypes[this.worldTypeId] == null ||
					   !WorldType.worldTypes[this.worldTypeId].getCanBeCreated())
				{
					++this.worldTypeId;

					if (this.worldTypeId >= WorldType.worldTypes.length)
					{
						this.worldTypeId = 0;
					}
				}

				this.generatorOptionsToUse = "";
				this.updateButtonText();
				this.func_82288_a(this.moreOptions);
			}
			else if (par1GuiButton.id == 6)
			{
				this.commandsToggled = true;
				this.commandsAllowed = !this.commandsAllowed;
				this.updateButtonText();
			}
			else if (par1GuiButton.id == 8)
			{
				WorldType.worldTypes[this.worldTypeId].onCustomizeButton(this.mc, this);
			}
		}
	}

	private void func_82287_i()
	{
		this.func_82288_a(!this.moreOptions);
	}

	private void func_82288_a(boolean par1)
	{
		this.moreOptions = par1;
		this.buttonGameMode.drawButton = !this.moreOptions;
		this.buttonGenerateStructures.drawButton = this.moreOptions;
		this.buttonBonusItems.drawButton = this.moreOptions;
		this.buttonWorldType.drawButton = this.moreOptions;
		this.buttonAllowCommands.drawButton = this.moreOptions;
		this.buttonCustomize.drawButton = this.moreOptions && (WorldType.worldTypes[this.worldTypeId].isCustomizable());

		if (this.moreOptions)
		{
			this.moreWorldOptions.displayString = I18n.getString("gui.done");
		}
		else
		{
			this.moreWorldOptions.displayString = I18n.getString("selectWorld.moreWorldOptions");
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2)
	{
		if (this.textboxWorldName.isFocused() && !this.moreOptions)
		{
			this.textboxWorldName.textboxKeyTyped(par1, par2);
			this.localizedNewWorldText = this.textboxWorldName.getText();
		}
		else if (this.textboxSeed.isFocused() && this.moreOptions)
		{
			this.textboxSeed.textboxKeyTyped(par1, par2);
			this.seed = this.textboxSeed.getText();
		}

		// super
		if (par2 == 28 || par2 == 156)
		{
			this.actionPerformed((GuiButton) this.buttonList.get(0));
		}

		((GuiButton) this.buttonList.get(0)).enabled = this.textboxWorldName.getText().length() > 0;
		this.makeUseableName();
	}

	/**
	 * Called when the mouse is clicked.
	 */
	protected void mouseClicked(int par1, int par2, int par3)
	{
		// super
		if (par3 == 0)
		{
			for (Object aButtonList : this.buttonList)
			{
				GuiButton guibutton = (GuiButton) aButtonList;

				if (guibutton.mousePressed(this.mc, par1, par2))
				{
					ObfuscationReflectionHelper.setPrivateValue(GuiScreen.class, this, guibutton, "selectedButton", "field_73883_a");
					this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
					this.actionPerformed(guibutton);
				}
			}
		}
		// ....

		if (this.moreOptions)
		{
			this.textboxSeed.mouseClicked(par1, par2, par3);
		}
		else
		{
			this.textboxWorldName.mouseClicked(par1, par2, par3);
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.getString("selectWorld.create"), this.width / 2, 20, 16777215);

		if (this.moreOptions)
		{
			this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterSeed"), this.width / 2 - 100, 47, 10526880);
			this.drawString(this.fontRenderer, I18n.getString("selectWorld.seedInfo"), this.width / 2 - 100, 85, 10526880);
			this.drawString(this.fontRenderer, I18n.getString("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, 10526880);
			this.drawString(this.fontRenderer, I18n.getString("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, 10526880);
			this.textboxSeed.drawTextBox();
		}
		else
		{
			this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
			this.drawString(this.fontRenderer, I18n.getString("selectWorld.resultFolder") + " " + this.folderName, this.width / 2 - 100, 85,
							10526880);
			this.textboxWorldName.drawTextBox();
			this.drawString(this.fontRenderer, this.gameModeDescriptionLine1, this.width / 2 - 100, 137, 10526880);
			this.drawString(this.fontRenderer, this.gameModeDescriptionLine2, this.width / 2 - 100, 149, 10526880);
		}

		for (Object aButtonList : this.buttonList)
		{
			GuiButton guibutton = (GuiButton) aButtonList;
			guibutton.drawButton(this.mc, par1, par2);
		}
	}

	public void func_82286_a(WorldInfo par1WorldInfo)
	{
		this.localizedNewWorldText = I18n.getStringParams("selectWorld.newWorld.copyOf", par1WorldInfo.getWorldName());
		this.seed = par1WorldInfo.getSeed() + "";
		this.worldTypeId = par1WorldInfo.getTerrainType().getWorldTypeID();
		this.generatorOptionsToUse = par1WorldInfo.getGeneratorOptions();
		this.generateStructures = par1WorldInfo.isMapFeaturesEnabled();
		this.commandsAllowed = par1WorldInfo.areCommandsAllowed();

		if (par1WorldInfo.isHardcoreModeEnabled())
		{
			this.gameMode = "hardcore";
		}
		else if (par1WorldInfo.getGameType().isSurvivalOrAdventure())
		{
			this.gameMode = "survival";
		}
		else if (par1WorldInfo.getGameType().isCreative())
		{
			this.gameMode = "creative";
		}
	}
}
