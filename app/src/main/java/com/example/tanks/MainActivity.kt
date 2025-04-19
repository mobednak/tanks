package com.example.tanks

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_SPACE
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import com.example.tanks.databinding.ActivityMainBinding
import com.example.tanks.drawers.BulletDrawer
import com.example.tanks.drawers.ElementsDrawer
import com.example.tanks.drawers.EnamyDrawer
import com.example.tanks.drawers.GritDrawer
import com.example.tanks.enums.Direction
import com.example.tanks.enums.Direction.DOWN
import com.example.tanks.enums.Direction.LEFT
import com.example.tanks.enums.Direction.RIGHT
import com.example.tanks.enums.Direction.UP
import com.example.tanks.enums.Material.BRICK
import com.example.tanks.enums.Material.CONCRETE
import com.example.tanks.enums.Material.EAGLE
import com.example.tanks.enums.Material.EMPTY
import com.example.tanks.enums.Material.GRASS
import com.example.tanks.enums.Material.PLAYER_TANK
import com.example.tanks.models.Coordinate
import com.example.tanks.models.Element
import com.example.tanks.models.Tank

const val CELL_SIZE = 50
lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var editMode = false

    private lateinit var playerTank: Tank
    private lateinit var eagle: Element

    private fun createTank(elementWidth: Int, elementHeight: Int): Tank {
        playerTank = Tank(
            Element(
                material = PLAYER_TANK,
                coordinate = getPlayerTankCoordinate(elementWidth, elementHeight)
            ), UP, BulletDrawer(binding.container, elementsDrawer.elementsOnCoordinate)
        )
        return playerTank
    }

    private fun createEagle(elementWidth: Int, elementHeight: Int): Element {
        eagle = Element(
            material = EAGLE,
            coordinate = getEagleCoordinate(elementWidth, elementHeight)
        )
        return eagle
    }

    private fun getPlayerTankCoordinate(width: Int, height: Int) = Coordinate(
        top = (height - height % 2) - (height - height % 2) % CELL_SIZE - PLAYER_TANK.height * CELL_SIZE,
        left = (width - width % (2 * CELL_SIZE)) / 2 * CELL_SIZE - PLAYER_TANK.width  * CELL_SIZE
    )

    private fun getEagleCoordinate(width: Int, height: Int) = Coordinate(
        top = (height - height % 2) - (height - height % 2) % CELL_SIZE - EAGLE.height * CELL_SIZE,
        left = (width - width % (2 * CELL_SIZE)) / 2 - EAGLE.width / 2 * CELL_SIZE
    )

    private val gritDrawer by lazy {
        GritDrawer(binding.container)
    }
    private val elementsDrawer by lazy {
        ElementsDrawer(binding.container)
    }


    private val levelStorage by lazy {
        LevelStorage(this)
    }

    private val enemyDrawer by lazy {
        EnamyDrawer(binding.container, elementsDrawer.elementsOnCoordinate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Menu"

        binding.editorClear.setOnClickListener { elementsDrawer.currentMaterial = EMPTY }
        binding.editorBrick.setOnClickListener { elementsDrawer.currentMaterial = BRICK }
        binding.editorConcrete.setOnClickListener { elementsDrawer.currentMaterial = CONCRETE }
        binding.editorGrass.setOnClickListener { elementsDrawer.currentMaterial = GRASS }
        binding.container.setOnTouchListener { _, event ->
            if (!editMode) {
                return@setOnTouchListener true
            }
            elementsDrawer.onTouchContainer(event.x, event.y)
        return@setOnTouchListener true
        }
        elementsDrawer.drawElementsList(levelStorage.loadLevel())
        elementsDrawer.drawElementsList(listOf(playerTank.element, eagle))
        hideSettings()
        countWidthHeight()
        }

    private fun countWidthHeight() {
        val frameLayout = binding.container
        frameLayout.viewTreeObserver
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    frameLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val elementWidth = frameLayout.width
                    val elementHeight = frameLayout.height
                    playerTank = createTank(elementWidth, elementHeight)
                    eagle = createEagle(elementWidth, elementHeight)
                    elementsDrawer.drawElementsList(listOf(playerTank.element, eagle))
                }
            })
    }

    private fun switchEditMode() {
        editMode = !editMode
        if (editMode) {
            showSettings()
        } else {
            hideSettings()
        }
    }

    private fun showSettings() {
        gritDrawer.drawGrit()
        binding.materialsContainer.visibility = VISIBLE
    }

    private fun hideSettings() {
        gritDrawer.remaveGrit()
        binding.materialsContainer.visibility = INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                switchEditMode()
                return true
            }

            R.id.menu_save -> {
                levelStorage.seveLevel(elementsDrawer.elementsOnCoordinate)
                return true
            }

            R.id.memu_play -> {
                startTheGame()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startTheGame() {
        if (editMode) {
            return
        }
        enemyDrawer.startEnemyCreation()
        enemyDrawer.moveEnemyTanks()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KEYCODE_DPAD_UP -> move(UP)
            KEYCODE_DPAD_DOWN -> move(DOWN)
            KEYCODE_DPAD_LEFT -> move(LEFT)
            KEYCODE_DPAD_RIGHT -> move(RIGHT)
            KEYCODE_SPACE -> {
                playerTank.bulletDrawer.makeBulletMove(
                    playerTank,
                    elementsDrawer.elementsOnCoordinate
                )
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun move(direction: Direction) {
        playerTank.move(direction, binding.container, elementsDrawer.elementsOnCoordinate)
    }

}
